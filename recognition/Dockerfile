FROM python:3.8.18
COPY . /app

RUN apt-get update
RUN pip install --upgrade pip
RUN apt-get install ffmpeg libsm6 libxext6 -y
RUN apt-get install pkg-config libmariadb-dev-compat -y

RUN pip install -r /app/requirements.txt
RUN python3 /app/modules/download.py
WORKDIR /app

CMD ["gunicorn", "-w", "4", "-b", "0.0.0.0:5000", "deepface_server:app"]