const express = require('express');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = 3000;

app.use((req, res, next) => {
    console.log(`[${new Date().toISOString()}] Incoming request: ${req.method} ${req.url}`);
    next();
});


const dataFilePath = path.join(__dirname, 'device_info.json');

if (!fs.existsSync(dataFilePath)) {
    try {
        fs.writeFileSync(dataFilePath, JSON.stringify([]), 'utf8');
        console.log(`[Server Setup] Created new data file: ${dataFilePath}`);
    } catch (writeErr) {
        console.error(`[Server Setup] ERROR: Could not create data file at ${dataFilePath}:`, writeErr);
    }
} else {
    console.log(`[Server Setup] Data file already exists: ${dataFilePath}`);
}

app.get('/device-info', (req, res) => {
    const deviceInfo = req.query;

    if (!deviceInfo || Object.keys(deviceInfo).length === 0) {
        console.warn(`[GET /device-info] WARNING: No device information provided in query parameters.`);
        return res.status(400).json({ message: 'No device information provided in URL query parameters.' });
    }

    console.log(`[GET /device-info] Received device info from query:`, deviceInfo);

    const newDeviceInfoEntry = { ...deviceInfo, timestamp: new Date().toISOString() };

    fs.readFile(dataFilePath, 'utf8', (err, data) => {
        if (err) {
            console.error(`[GET /device-info] ERROR reading data file '${dataFilePath}':`, err);
            return res.status(500).json({ message: 'Internal server error: Failed to read data.' });
        }

        let devices = [];
        try {
            devices = JSON.parse(data);
            if (!Array.isArray(devices)) {
                console.warn(`[GET /device-info] WARNING: Data file is not a JSON array. Resetting file.`);
                devices = [];
            }
        } catch (parseErr) {
            console.error(`[GET /device-info] ERROR parsing JSON from data file '${dataFilePath}':`, parseErr);
            console.warn(`[GET /device-info] Resetting data file due to parse error.`);
            devices = [];
        }

        devices.push(newDeviceInfoEntry);

        fs.writeFile(dataFilePath, JSON.stringify(devices, null, 2), 'utf8', (writeErr) => {
            if (writeErr) {
                console.error(`[GET /device-info] ERROR writing data to file '${dataFilePath}':`, writeErr);
                if (writeErr.code === 'EACCES') {
                    console.error(`[GET /device-info] PERMISSION DENIED: Cannot write to file. Check file permissions.`);
                }
                return res.status(500).json({ message: 'Failed to save device information.' });
            }
            console.log(`[GET /device-info] Device info saved successfully to ${dataFilePath}!`);
            res.status(200).json({ message: 'Device information received and saved via GET.' });
        });
    });
});

app.get('/', (req, res) => {
    res.send('Node.js backend for Tic-Tac-Toe app is running!');
});

app.use((req, res) => {
    console.warn(`[Unhandled Request] No route found for: ${req.method} ${req.url}`);
    res.status(404).json({ message: `Cannot ${req.method} ${req.url}` });
});

app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
    console.log(`Device info will be stored in ${dataFilePath}`);
});