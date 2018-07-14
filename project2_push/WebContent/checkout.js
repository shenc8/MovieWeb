function handleLoginResult(resultData) {
	let table = jQuery("#checkout_form");
	if (resultData[0]["success"]=="Yes")
	{
		document.getElementById("checkout_form").innerHTML = "<p>"+resultData[0]["message"]+"</p>"
			+"<a href=index.html>Go Back to Main Page</a>"
		}
	else
	{
		console.log("Fail in checkout");
		document.getElementById("result").innerHTML="<p>Failed, Try again</p>";
	}
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitCheckoutForm(formSubmitEvent) {
    console.log("submit checkout form");
    
    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/checkout",
        // Serialize the login form to the data sent by POST request
        jQuery("#checkout_form").serialize(),
        (resultDataString) => handleLoginResult(resultDataString));
    console.log("success?")

}
jQuery("#checkout_form").submit((event) => submitCheckoutForm(event));