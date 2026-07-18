const express = require('express');
const app = express();
const port = 8080;

app.use(express.json());

app.post('/secure-data', (req, res) => {
    res.status(200).send({ status: "Processed inside a hardened container wrapper." });
});

app.get('/healthz', (req, res) => {
    res.status(200).send("Secure compute node active.");
});

app.listen(port, () => {
    console.log(`Hardened application executing on port ${port}`);
});