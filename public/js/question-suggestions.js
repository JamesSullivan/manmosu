$(document).ready(
		(function qSuggestions(){
			var suggestionsContainer = $("#question-suggestions");
			
			function getQuestionSuggestions(data) {
				if (data.trim() == "") {
					suggestionsContainer.hide();
					return;
				}
				
				suggestionsContainer.show();
				suggestionsContainer.find(".suggested-questions-list").html(data);
			}
			
			var timeoutId;
			var questionTitle = $("#title");
			questionTitle.keyup(function () {
				var currentTypedValue = questionTitle.val();
				if (currentTypedValue.length >= 5) {
					clearTimeout(timeoutId);
					timeoutId = setTimeout(function () {
						$.get("/questionSuggestion", {query: currentTypedValue, limit: 3}, getQuestionSuggestions);
					}, 1000);
				} else {
					suggestionsContainer.hide();
				}
			});
		})());