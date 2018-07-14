function handleSearchResult(resultDataString) {
    resultDataJson = resultDataString;
    console.log(resultDataJson["message"]);
    jQuery("#insert_message").text(resultDataJson["message"]);
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitSearchForm(formSubmitEvent) {
    console.log("submit insert star form");
    
    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.get(
        "api/insert_star",
        // Serialize the login form to the data sent by POST request
        jQuery("#star_form").serialize(),
        (resultDataString) => handleSearchResult(resultDataString));

}

// Bind the submit action of the form to a handler function
jQuery("#star_form").submit((event) => submitSearchForm(event));
