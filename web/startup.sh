#!/bin/bash

# Navigate to the app directory
cd /home/site/wwwroot

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
  npm ci --production
fi

# Start Next.js in production mode
PORT=${PORT:-8080} npm start
