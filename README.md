mvnw.cmd clean package(rode na pasta raiz do projeto para gerar o arquivo api.war na pasta targe)
Usar Tomcat Servlet no Senai
https://tomcat.apache.org/download-10.cgi(Baixar windows 64 bit)
extrair
Pegar o arquivo api.war e colocar na pasta webapps
Abrir a pasta bin e pegar o caminho
dar cd <caminho> no terminal
dar catalina.bat run
abrir os endpoints
GET http://localhost:8080/api/api/contatos (todos os users)
GET http://localhost:8080/api/api/contatos/{id} (User por ID)
POST http://localhost:8080/api/api/contatos (Adicionar usuário)
PUT http://localhost:8080/api/api/contatos/{id} (Editar usuário)
DELETE http://localhost:8080/api/api/contatos/{id} (Deletar usuário)
