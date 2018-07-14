function handleSearchResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);
    console.log(resultDataJson["message"]);
    jQuery("#insert_message").text(resultDataJson["message"]);
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitSearchForm(formSubmitEvent) {
    console.log("submit insert movie form");
    
    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.get(
        "api/insert_movie",
        // Serialize the login form to the data sent by POST request
        jQuery("#insert_form").serialize(),
        (resultDataString) => handleSearchResult(resultDataString));

}

// Bind the submit action of the form to a handler function
jQuery("#insert_form").submit((event) => submitSearchForm(event));
