$(function(){
	$(".mark-as-solution").on('click', markAsSolution);
	
	function markAsSolution(event){
		event.preventDefault();
		var mark = $(this);
		var markParent = $(mark).parent();
		var oldSolutionParent = $(".solution-container");
		var oldSolution = $(oldSolutionParent).find(".mark-as-solution");

		var token =  $('input[name="csrfToken"]').attr('value')

		$.ajax({
			url:mark.attr("href"),
			method: "POST",
			beforeSend: function(xhr) {
				xhr.setRequestHeader('Csrf-Token', token);
				toggleAll();
			},
			error: function(jqXHR) {
				toggleAll();
				errorPopup("error occured", mark, "center-popup");
				console.log(jqXHR);
			}
		});
		
		function toggleAll(){
			if(notOldSolution()) toggleClassesOf(oldSolutionParent);
			toggleClassesOf(markParent);
		}
		
		function notOldSolution(){
			return mark[0] != oldSolution[0];
		}
		
		function toggleClassesOf(parent){
			parent.toggleClass("solution-container").toggleClass("not-solution-container");
		}
	};
	
});
