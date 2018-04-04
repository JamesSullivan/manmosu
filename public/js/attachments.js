function handleFileSelect(evt) {
    var files = evt.target.files; // FileList object

    // files is a FileList of File objects. List some properties.
    var output = [];
    for (var i = 0, f; f = files[i]; i++) {
      output.push('<li><strong>', escape(f.name), '</strong> (', f.type || 'n/a', ') - ',
                  f.size, ' bytes, last modified: ',
                  f.lastModifiedDate ? f.lastModifiedDate.toLocaleDateString() : 'n/a',
                  '</li>');
    }
    document.getElementById('list').innerHTML = '<ul>' + output.join('') + '</ul>';
    document.getElementById('uploadclear').setAttribute('class', 'visible');
  }
  document.getElementById('files').addEventListener('change', handleFileSelect, false);
var token =  $('input[name="csrfToken"]').attr('value')
function callUpload() {
	console.log("Made it to ajaxCallUpload")
	if($("#files")[0].files.length > 0){
	var formData = new FormData($("#uploadForm")[0]);
	console.log($("#uploadForm")[0])
	$.ajax({
		type: "POST",
		url: '/upload',
		beforeSend: function(xhr) {
	  	  xhr.setRequestHeader('Csrf-Token', token);
		},
		headers: { 'IsAjax': 'true' },
		dataType: "json",
		processData: false,
		contentType: false,
		data: formData,
		success: function (result) {
			console.log(data);
		},
		error: function (error) {
			console.log(error);
		}
	});
	}
}