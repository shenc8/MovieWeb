function handleSearchResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);
    console.log("handle search response");
    console.log(resultDataJson);   
    window.open("search_result.html?title="+resultDataJson["title"]+"&year="+resultDataJson["year"]+"&director="+resultDataJson["director"]+"&star="+resultDataJson["star"]+"&page=1")
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitSearchForm(formSubmitEvent) {
    console.log("submit search form");
    
    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.get(
        "api/search",
        // Serialize the login form to the data sent by POST request
        jQuery("#search_form").serialize(),
        (resultDataString) => handleSearchResult(resultDataString));

}

// Bind the submit action of the form to a handler function
jQuery("#search_form").submit((event) => submitSearchForm(event));

