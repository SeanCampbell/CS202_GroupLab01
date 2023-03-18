FROM python:3.11

COPY . /password_login
WORKDIR /password_login

RUN pip install flask

CMD python main.py
