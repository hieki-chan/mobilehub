from fastapi import FastAPI, UploadFile, File
from fastapi.responses import RedirectResponse
from fastapi.middleware.cors import CORSMiddleware
from ocr import extract_cccd_from_image
from cccd_db import validate_cccd
from io import BytesIO

app = FastAPI(title="Vietnamese ID OCR API", version="1.0")

# --- CORS config ---
origins = [
    "http://localhost:5173",
    "http://localhost:5174",
    "https://mobilehub-website.vercel.app"
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],   # GET, POST, ...
    allow_headers=["*"],   # Content-Type, Authorization, ...
)

# --- routes ---
@app.get("/", include_in_schema=False)
async def root():
    return RedirectResponse(url="/docs")

@app.post("/api/v1/extract-cccd")
async def extract_cccd(front_image: UploadFile = File(...)):
    try:
        front_bytes = await front_image.read()
        front_info = extract_cccd_from_image(BytesIO(front_bytes))

        front_cccd = front_info.get("cccd")

        db_info = validate_cccd(front_cccd)
        if not front_cccd or not db_info:
            return {"status": "invalid"}

        return {
            "status": "ok",
            "info": db_info
        }

    except Exception as e:
        return {"status": "error", "error": str(e)}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=9096)
