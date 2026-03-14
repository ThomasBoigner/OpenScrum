document.body.addEventListener('htmx:configRequest', function(event) {
    let token = document.querySelector('meta[name="_csrf"]').content;
    let header = document.querySelector('meta[name="_csrf_header"]').content;
    event.detail.headers[header] = token;
});