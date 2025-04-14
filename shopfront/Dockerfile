# Use the official Bun image (based on Alpine)
FROM oven/bun:alpine

# Maintainer information
LABEL maintainer="Manav Chaudhary <manavchaudhary041@gmail.com>"

# Set working directory
WORKDIR /app

# Copy bun.lockb and package.json separately for better caching
COPY bun.lockb package.json ./

# Install dependencies
RUN bun install

# Copy the rest of your app
COPY . .

# Default command (you can override in docker-compose or CLI)
ENTRYPOINT ["bun", "run", "dev"]