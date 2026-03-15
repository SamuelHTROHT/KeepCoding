-- Enunciado 1--
--1.--
select
	count(*) as numero_registros
from flights;

--2.--
select 
	count (distinct unique_identifier) as numero_vuelos
from flights;

--3.--
with base as(
	
	select 
		unique_identifier
	from flights
	group by unique_identifier
	having count (flight_row_id) > 1
)
select 
	count(*)
from base;


--Enunciado 2--
select 
	*
from flights
order by unique_identifier asc;

/*Tras analizaar los registros que contiene la base de datos he llegado a la conclusión
de que esta base de datos por lo general tiene 2 registros o mas de cada vuelo uno con 
lo planeado, pero con los registros a null ya que el vuelo todavía no ha sido realizado,
otro con las horas de salida y llegada ya actualizadas que se registra una vez el vuelo
ha finalizado, llevando así un registro tanto de lo que estaba planeado como de como
ha sido el vuelo realmente, además a veces un vuelo tiene varios registros sin los datos
actualizados solo con lo planeado que actualizan posteriormente a la creación del registro
planeado del vuelo, creo que se puede deber a algun cambio o reenvío de información de la
aerolinea que no se refleja en el registro o a una ingesta de nuevo de la BBDD operativa 
hacia la analitica sin que el vuelo se haya realizado.
*/



--Enunciado 3--

--1--
with base as(
	select
		flight_row_id,
		unique_identifier,
		updated_at,
		created_at
	from flights
),
dif_created as (
	select
		unique_identifier,
		count(distinct created_at) as diferentes_created
	from base
	group by unique_identifier
	having count(distinct created_at) > 1
)

select 
	*
from dif_created;

--2--

with base as(
	select
		flight_row_id,
		unique_identifier,
		updated_at,
		created_at
	from flights
),

dif_updated as(
	select
		*
	from base
	where updated_at < created_at
)
select 
	*
from dif_updated;
--Parece que los datos son muy consistentes excluyendo el problema de que hay varios nulos,-- 
--ya que indican que no hay ni created at mayores que updated at ni varios created at--

--Enunciado 4--
create table flights_data_updated as
	with base as(
		select 
			*,
			row_number() over(partition by unique_identifier order by updated_at desc) as rn
		from flights
	)
	select 
		*
	from base
	where rn = 1;


--Enunciado 5--

select
	flight_row_id,
	local_departure,
	local_actual_departure,
	local_arrival,
	local_actual_arrival,
	case
		when local_departure is null then created_at
		else local_departure
	end as effective_local_departure,
	
	case 
		when local_actual_departure is null and local_departure is not null then local_departure
		when local_actual_departure is null and local_departure is null then created_at
		else local_actual_departure
	end as effective_local_actual_departure,
	
	case
		when local_arrival is null then created_at
		else local_arrival
	end as effective_local_arrival,
	
	case 
		when local_actual_arrival is null and local_arrival is not null then local_arrival
		when local_actual_arrival is null and local_arrival is null then created_at
		else local_actual_arrival
	end as effective_local_actual_arrival
from flights_data_updated;

--Enunciado 6--
select 
	distinct arrival_status
from flights_data_updated;

select
	arrival_status,
	count(*)
from flights_data_updated
group by arrival_status;

select
	* 
from flights_data_updated;

-- de primera observamos de forma sencilla que DY y OT corresponden a vuelos delayed y on time refiriendose a la hora de llegada--
select
	*
from flights_data_updated
where arrival_status = 'EY' or arrival_status = 'NS' or arrival_status = 'CX'
order by arrival_status asc;

/* tras observar bien los que tienen menos vuelos en esta bbdd y los casos mas especiales, he llegado a la conclusión de que
CX significa cancelado, ya que nunca tienen hora de salida ni de llegada ya que se cancelarian ni tiempo de delay.
EY significa vuelos que han llegado early ya que si nos fijamos tiene los delay_mins muy altos en valores negativos.
y NS significa que son vuelos con no status ya que siguen volando porque si que tienen hora de salida pero todavia no de llegada en la mayor parte de los casos.
*/


--Enunciado 7--
--1--
select 
	fli.flight_row_id,
	fli.unique_identifier,
	fli.departure_airport,
	air.country
from flights_data_updated as fli
left join airports as air
on fli.departure_airport = air.airport_code;
--2--
select
	air.country,
	count(*) as vuelos_despegan_pais
from flights_data_updated as fli
left join airports as air
on fli.departure_airport = air.airport_code
group by air.country;


--Enunciado 8--
--1--
select
	air.country,
	round(avg(fli.delay_mins), 2) as delay_medio_pais
from flights_data_updated as fli
left join airports as air
on fli.departure_airport = air.airport_code
group by air.country;

--2--
select
	air.country,
	fli.arrival_status,
	count(*) as estado_llegada_pais
from flights_data_updated as fli
left join airports as air
on fli.departure_airport = air.airport_code
group by air.country, arrival_status;

--Enunciado 9--
with base as(
	select
		*,
		extract(month from local_actual_departure) as mes_salida
	from flights_data_updated
),
asignar_estacion as(
	select
		*,
		case
			when mes_salida in(12, 1, 2) then 'INVIERNO'
			when mes_salida between 3 and 5 then 'PRIMAVERA'
			when mes_salida between 6 and 8 then 'VERANO'
			when mes_salida between 9 and 11 then 'OTONO'
		end as estacion
	from base
)
select
	air.country,
	asi.estacion,
	round(avg(asi.delay_mins), 2) as delay_medio_pais
from asignar_estacion as asi
left join airports as air
on asi.departure_airport = air.airport_code
group by air.country, asi.estacion
order by delay_medio_pais asc;

-- parece que en verano y otoño hay mas delay medio pero no parecen datos que lo confirmen al 100% ya que depende tambien del pais


--Enunciado 10--
with base as (
    select 
        unique_identifier,
        departure_airport,
        updated_at,
        lag(updated_at) over (
            partition by unique_identifier 
            order by updated_at
        ) as anterior_updated_at
    from flights
),
diferencias as (
    select 
        departure_airport,
        updated_at - anterior_updated_at AS diferencia_tiempo
    from base
    where anterior_updated_at is not null
)
select 
    air.country,
    dif.departure_airport,
    avg(dif.diferencia_tiempo) as media_actualizacion
from diferencias as dif
left join airports as air 
on dif.departure_airport = air.airport_code
group by air.country, dif.departure_airport
order by media_actualizacion asc;

--Enunciado 11--

--1--
with base as(
	select 
	*,
	split_part(unique_identifier,'-', 1) as aerolinea_id,
	split_part(unique_identifier,'-', 2) as numero_vuelo_id,
	split_part(unique_identifier,'-', 3) as fecha_id,
	split_part(unique_identifier,'-', 4) as aeropuerto_salida_id,
	split_part(unique_identifier,'-', 5) as aeropuerto_llegada_id
	from flights_data_updated
),
flag as(
	select 
	*,
	case 
		when trim(aerolinea_id) = trim(airline_code) 
		and trim(fecha_id) = trim(to_char(local_departure, 'YYYYMMDD'))
		and trim(aeropuerto_salida_id) = trim(departure_airport)
		and trim(aeropuerto_llegada_id) = trim(arrival_airport) 
		then true
		else false
	end as is_consistent
	from base
)
select
	is_consistent,
	count(*)
from flag
group by is_consistent;

--2--
with base as(
	select 
	*,
	split_part(unique_identifier,'-', 1) as aerolinea_id,
	split_part(unique_identifier,'-', 2) as numero_vuelo_id,
	split_part(unique_identifier,'-', 3) as fecha_id,
	split_part(unique_identifier,'-', 4) as aeropuerto_salida_id,
	split_part(unique_identifier,'-', 5) as aeropuerto_llegada_id
	from flights_data_updated
),
flag as(
	select 
	*,
	case 
		when trim(aerolinea_id) = trim(airline_code) 
		and trim(fecha_id) = trim(to_char(local_departure, 'YYYYMMDD'))
		and trim(aeropuerto_salida_id) = trim(departure_airport)
		and trim(aeropuerto_llegada_id) = trim(arrival_airport) 
		then true
		else false
	end as is_consistent
	from base
)
select 
	air.airline_code,
	air.name,
	count (*)
from flag as fla
left join airlines as air
on fla.airline_code = air.airline_code
where fla.is_consistent = false
group by fla.is_consistent, air.airline_code;
