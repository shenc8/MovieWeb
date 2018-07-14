function HashMap()
{
	var object = new Object();
	this.contains=function(key){
		if (key in object)
			return true;
		else
			return false;
	}
	this.put=function(key,value){
		object[key]=value;
	}
	this.get=function(key){
		return object[key];
	}
}
var cache = new HashMap()
function handleSearchResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);
    console.log("handle direct search response");
    console.log(resultDataJson);   
    window.location.assign("search_result.html?title="+resultDataJson["title"]+"&year=&director=&star=&page=1")
}
function handleLookup(query, doneCallback) {
	console.log("Autocomplete initiated")
	if (cache.contains(query))
	{
		console.log("Cache handle")
		handleLookupAjaxSuccess(cache.get(query), query, doneCallback)
	}
	else
	{
	jQuery.ajax({
		"method": "GET",
		"url": "auto-suggestion?query=" + escape(query),
		"success": function(data) {
			console.log("Ajax Handle")
			handleLookupAjaxSuccess(data, query, doneCallback) 
			cache.put(query,data)
		},
		"error": function(errorData) {
			console.log("lookup ajax error")
			console.log(errorData)
		}
	})
	}
}
function handleLookupAjaxSuccess(data, query, doneCallback) {
	var jsonData = JSON.parse(data);
	console.log("Suggestion list")
	console.log(jsonData)
	doneCallback( { suggestions: jsonData } );
}
function handleSelectSuggestion(suggestion) {	
	var url = "movie.html?title="+suggestion["data"]["title"]+"&year=&director=&star=&page=1"
	window.location.assign(url);
}
$('#autocomplete').autocomplete({
    lookup: function (query, doneCallback) {
    		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    groupBy: "category",
    deferRequestBy: 300,
    minChars: 3
});
function submitSearchForm(formSubmitEvent) {
    console.log("Handle normal search");
    formSubmitEvent.preventDefault();
    jQuery.get(
        "api/index",
        jQuery("#search_form").serialize(),
        (resultDataString) => handleSearchResult(resultDataString));

}

jQuery("#search_form").submit((event) => submitSearchForm(event));

$('#autocomplete').keypress(function(event) {
	if (event.keyCode == 13) {
		submitSearchForm($('#autocomplete').val())
	}
})