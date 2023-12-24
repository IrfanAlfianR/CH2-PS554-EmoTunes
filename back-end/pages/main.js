document.getElementById('searchForm').addEventListener('submit', function (event) {
    event.preventDefault();
  
    const genreInput = document.getElementById('genreInput').value;
    fetch('https://emotunesproject.et.r.appspot.com/playlist', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ emotion: genreInput }),
    })
      .then(response => response.json())
      .then(data => {
        // console.log('Data dari Server:', data.data);
        updateTable(data);
      })
      .catch(error => {
        console.error('Error:', error.message);
      });
});

function updateTable(data) {
    const tableBody = document.getElementById('tableBody');
    tableBody.innerHTML = '';

    data.data.forEach(item => {
      const row = tableBody.insertRow();
      const cell1 = row.insertCell(0);
      const cell2 = row.insertCell(1);
      const cell3 = row.insertCell(2);
      const cell4 = row.insertCell(3);

      cell1.textContent = item.title; 
      cell2.textContent = item.artist.name; 
      cell3.innerHTML = `<audio controls>
                          <source src="${item.preview}" type="audio/mpeg">
                          Your browser does not support the audio element.
                        </audio>`;
      cell4.innerHTML = `<a href="${item.link}" target="_blank" class="btn btn-primary"><i class="fa fa-music"></i></a>`;
    });
}
