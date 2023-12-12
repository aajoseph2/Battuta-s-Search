function checkQuery() {
    var query = document.getElementById('query').value;

    if (query.trim() === '') {
        alert('Please enter a query before trying to search.');
        return false;
    }
    return true;
 }