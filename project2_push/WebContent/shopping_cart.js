function handleResult(resultData) {
	let rowHTML = "";
	for (let i = 0; i < resultData.length; i++) {
		console.log(i);
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th><form id="+resultData[i]["title"]+" method=\"get\" action='shopping_cart.html'>"
        	+'<input type="hidden" name="title" value='+resultData[i]["title"]+'>'
        	+'<input type="text" name="quantity" value="'
        	+ resultData[i]["quantity"] 
        	+ "\"><input type=\"submit\" value=\"update\"></form></th>";
        rowHTML += "<th><form id="+resultData[i]["title"]+" method=\"get\" action='shopping_cart.html'>" 
        +'<input type="hidden" name="title" value='+resultData[i]["title"]+'>'
        +'<input type="hidden" name="quantity" value="0">'
        +'<input type="submit" value="delete"></form>'
        +"</th>";
        rowHTML += "</tr>";
	}
	document.getElementById("shopping_table_body").innerHTML = rowHTML;
}
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/shopping_cart", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
//function submitUpdateForm(formSubmitEvent) {
//    console.log("submit update form");
//    formSubmitEvent.preventDefault();
//    console.log(jQuery("#update_form"));
//    jQuery.get(
//    		"api/shopping_cart",
//        // Serialize the login form to the data sent by POST request
//    	jQuery("#yes").serialize(),
//        (resultDataString) => handleResult(resultDataString));
//    console.log("after"+jQuery("#update_form"));
//}
jQuery("#screen-selector").on('submit', function(event) {
    var button = jQuery(event.target);
    console.log("submit update form");
    event.preventDefault();
    console.log(button);
    jQuery.get(
    		"api/shopping_cart",
    		button.serialize(),
        (resultDataString) => handleResult(resultDataString));
    console.log(button);
});
//	  
//
//function submitDeleteForm(formSubmitEvent) {
//    console.log("submit delete form");
//    formSubmitEvent.preventDefault();
//    jQuery.get(
//    		"api/shopping_cart",
//        // Serialize the login form to the data sent by POST request
//    	jQuery("#delete_form").serialize(),
//        (resultDataString) => handleResult(resultDataString));
//}
//jQuery("#delete_form").submit((event) => submitDeleteForm(event));