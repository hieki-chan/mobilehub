FROM python:3.10-slim

ENV DEBIAN_FRONTEND=noninteractive
ENV PYTHONUNBUFFERED=1

RUN apt-get update && apt-get install -y --no-install-recommends \
    libgl1 \
    libglib2.0-0 \
    libsm6 \
    libxrender1 \
    libxext6 \
    libgomp1 \
    libopenblas-dev \
    build-essential \
    && rm -rf /var/lib/apt/lists/* \
    && python -m pip install --upgrade pip setuptools wheel

WORKDIR /app

COPY app/requirements.txt .

RUN pip install --no-cache-dir numpy==1.26.4 \
    && pip install --no-cache-dir torch==2.1.2+cpu torchvision==0.16.2+cpu \
       --extra-index-url https://download.pytorch.org/whl/cpu \
    && pip install --no-cache-dir -r requirements.txt

COPY app/ .

EXPOSE 9096

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "9096"]
