require('dotenv').config();
const express = require('express');
const bodyParser = require('body-parser');
const axios = require('axios');
const app = express();
const cors = require('cors');
const port = process.env.PORT;

app.use(cors());
app.use(express.static('public'));
app.use(bodyParser.json());

const moodGenreMap = {
  "joy": "pop",
  "fear": "rock",
  "sadness": "indie",
  "anger": "metal",
  "love": "romance",
  "surprise": "electronic",
  "default": "pop", // Genre default jika mood tidak terdeteksi
};

app.post('/playlist', async (req, res) => {
  try {
    const emotion = (req.body.emotion || '').toLowerCase();
    const genre = moodGenreMap[emotion] || moodGenreMap.default;

    const apiKey = process.env.API_KEY;
    const searchUrl = `https://api.deezer.com/search?q=${genre}&output=json&limit=10`;

    const response = await axios.get(searchUrl, {
      headers: {
        'x-rapidapi-key': apiKey,
      },
    });

    res.json(response.data);
  } catch (error) {
    console.error('Error:', error.message);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

app.get('/playlist/:emotion', async (req, res) => {
  try {
    const emotion = req.params.emotion.toLowerCase();
    const genre = moodGenreMap[emotion] || moodGenreMap.default;

    const apiKey = process.env.API_KEY;
    const searchUrl = `https://api.deezer.com/search?q=${genre}&output=json&limit=10`;

    const response = await axios.get(searchUrl, {
      headers: {
        'x-rapidapi-key': apiKey,
      },
    });

    res.json(response.data);
  } catch (error) {
    console.error('Error:', error.message);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

app.listen(port, () => {
  console.log(`Server berjalan di http://localhost:${port}`);
});
