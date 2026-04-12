readme_content = """# Análisis de la Demanda de Airbnb en Madrid

Este proyecto consiste en un dashboard interactivo desarrollado en **Power BI** para analizar los factores que influyen en el éxito de los alquileres vacacionales en la ciudad de Madrid, utilizando el dataset de *Airbnb Listings*.

## 🎯 Objetivo del Proyecto
El objetivo principal es responder a la pregunta de negocio: **"¿Cómo influyen el precio, la ubicación, los metros cuadrados y número de reseñas en la demanda de alojamientos en Madrid?"**.

## 🛠️ Proceso de Datos (ETL)
Se ha realizado una limpieza y transformación profunda mediante **Power Query (M)**:
- **Filtrado Geográfico:** Selección exclusiva de registros pertenecientes a la ciudad de Madrid y su comunidad.
- **Corrección de Coordenadas:** Ajuste de tipos de datos para Latitud y Longitud, asegurando la precisión decimal para una correcta geolocalización.

## 📊 Medidas DAX y Análisis
Se han implementado cálculos avanzados para enriquecer el análisis:
- **Precio Medio:** Cálculo dinámico del coste por noche.
- **Precio por m²:** Conversión de *Square Feet* a metros cuadrados y relación con el precio promedio.
- **Evolución Temporal:** Análisis de la tendencia de calidad y volumen desde 2010.

## 🖼️ Visualizaciones Clave
El dashboard se compone de 5 vistas estratégicas:
1. **Mapa de Distribución:** Localización de la oferta y densidad de demanda por tamaño y precio por colores.
2. **Gráfico de Dispersión (Impacto del Precio):** Demostración visual de la correlación negativa entre precio y volumen de reservas añadiendo también el tamaño de los círculos siendo este el número de reseñas demostrando que cuantas más hay mas alquilan ese piso.
3. **Evolución Histórica:** Gráfico de líneas que compara la puntuación media vs. el crecimiento del mercado mediante las reseñas realizadas.
4. **Ranking de Barrios:** Análisis del precio del suelo (m²) por distrito y con el número de veces alquilado observamos los barrios en los que mas se alquila.
5. **Panel de KPIs:** Indicadores clave de rendimiento (Puntuación, Precio y Número de reseñas).

## 🚀 Conclusiones
El análisis revela que la demanda se concentra fuertemente en el distrito **Centro**, con una sensibilidad al precio muy marcada donde los alojamientos en el rango de 60€-90€ maximizan su rotación. Asimismo, se observa una estabilización de la calidad (puntuaciones >90) a pesar del crecimiento masivo de la oferta en los últimos años.

---
**Autor:** Samuel Hornero Terán 
**Herramientas:** Power BI Desktop, Power Query, DAX.
"""