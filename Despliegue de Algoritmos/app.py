from fastapi import FastAPI
from transformers import pipeline

app = FastAPI(
    title="API de Modelos ML", 
    description="Práctica final: FastAPI con Pipelines de Hugging Face"
)

# Carga de los dos pipelines de Hugging Face
sentiment_model = pipeline("sentiment-analysis")
generator_model = pipeline("text-generation", model="gpt2")

#Endpoint de bienvenida
@app.get("/")
def read_root():
    return {"mensaje": "API de Clasificación y NLP activa"}

#Estado del sistema
@app.get("/status")
def get_status():
    return {"status": "operativo", "modelos": ["sentiment-analysis", "summarization"]}

#Versión de la API
@app.get("/version")
def get_version():
    return {"version": "1.0.0"}

#Análisis de sentimientos
@app.get("/sentiment/{text}")
def analyze_sentiment(text: str):
    result = sentiment_model(text)
    return {"texto": text, "resultado": result}

#Generar de texto
@app.get("/generar/{text}")
def generate_text(text: str):
    result = generator_model(text, max_length=30, num_return_sequences=1)
    return {"texto_generado": result[0]['generated_text']}