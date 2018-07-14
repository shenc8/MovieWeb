
function handleLoginResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);
    console.log("Success");
    console.log("handle login response");
    console.log(resultDataJson['message']);
    if (resultDataJson["status"] == "success" ) {
    	if (resultDataJson["message"]=="TA_success")
    	{
    		window.location.replace("_dashboard.html");
    	}
    	else
    	{
    		window.location.replace("index.html");
    	}
    }
    else {
    	grecaptcha.reset();
        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#login_error_message").text(resultDataJson["message"]);
        
    }
}

function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/login",
        jQuery("#login_form").serialize(),
        (resultDataString) => handleLoginResult(resultDataString));

}
jQuery("#login_form").submit((event) => submitLoginForm(event));

