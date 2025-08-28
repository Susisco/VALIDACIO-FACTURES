# Etapa 1: build de l'app
FROM node:20-alpine as build
WORKDIR /app
COPY . .
RUN npm install && npm run build -- --mode production


# Etapa 2: executar amb vite preview
FROM node:20-alpine
WORKDIR /app
RUN npm install -g serve
COPY --from=build /app/dist .
EXPOSE 5173
CMD ["serve", "-l", "5173"]
