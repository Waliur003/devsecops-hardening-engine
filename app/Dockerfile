# Stage 1: Build footprint verification
FROM node:20-alpine AS builder
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm ci

# Stage 2: Hardened final container runtime layer
FROM node:20-alpine
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm ci --only=production
COPY --from=builder /usr/src/app/node_modules ./node_modules
COPY . .

# Enforce non-root execution boundaries at the container level
USER node

EXPOSE 8080
CMD ["npm", "start"]