
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["id"] + "</th>";
        rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "<th>" + resultData[i]["genres_list"] + "</th>";
        rowHTML += "<th>" + resultData[i]["stars_name"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    console.log("we have "+resultData[0]["number"]);
    document.getElementById("addcart_form").innerHTML = '<input type="hidden" name="add" value="true">'
    	+'<input type="submit" name="button" value="Add Cart">';
    if (resultData[0]["number"]==0)
	{	
		document.getElementById("cart_form").innerHTML = '<li><a>shopping cart</a></li>'+
	    '<li><a>'+resultData[0]["number"]+'</a></li>';
	}
	else
	{
	document.getElementById("cart_form").innerHTML = '<li><a href="shopping_cart.html">shopping cart</a></li>'+
    '<li><a>'+resultData[0]["number"]+'</a></li>';}
    console.log("add_cart");
}
function handleSubmitResult(resultData) {
	console.log("I have "+resultData[0]["number"]);
	if (resultData[0]["number"]==0)
	{	
		document.getElementById("cart_form").innerHTML = '<li><a>shopping cart</a></li>'+
	    '<li><a>'+resultData[0]["number"]+'</a></li>';
	}
	else
	{
	document.getElementById("cart_form").innerHTML = '<li><a href="shopping_cart.html">shopping cart</a></li>'+
    '<li><a>'+resultData[0]["number"]+'</a></li>';}
}
let title = getParameterByName('title');
function submitAddForm(formSubmitEvent) {
    console.log("submit add form");
    formSubmitEvent.preventDefault();
    jQuery.get(
    		"api/movie?title=" + title,
        // Serialize the login form to the data sent by POST request
        jQuery("#addcart_form").serialize(),
        (resultDataString) => handleSubmitResult(resultDataString));

}
jQuery("#addcart_form").submit((event) => submitAddForm(event));
/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL


// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/movie?title=" + title, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});