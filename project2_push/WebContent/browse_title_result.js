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

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"

    // append two html <p> created to the h3 body, which will refresh the page
    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#title_table_body");
    console.log(resultData[0]["has_next"]);
    console.log(resultData[0]["next_value"]);
    if (resultData[0]["has_next"] =="Y")
    {
    	if (resultData[0]["next_value"]==2)
    		{
    			document.getElementById("prag").innerHTML ="<li ><a>prev</a></li>\r\n"+
    					"<li ><a>"+(resultData[0]["next_value"]-1)+"</a></li>\r\n"+
    					"<li ><a href='"+"browse_title_result.html?title="+resultData[0]["title"]+"&page="+resultData[0]["next_value"]+"'>next</a></li>\r\n";
    		}
    	if (resultData[0]["next_value"]>2)
    		{
	    		document.getElementById("prag").innerHTML =
	    		"<li > <a href='"+"browse_title_result.html?title="+resultData[0]["title"]+"&page="+(resultData[0]["next_value"]-2)+"'>prev</a></li>\r\n"+
				"<li ><a>"+(resultData[0]["next_value"]-1)+"</a></li>\r\n"+
				"<li ><a href='"+"browse_title_result.html?title="+resultData[0]["title"]+"&page="+resultData[0]["next_value"]+"'>next</a></li>\r\n";
    		}
    }
    else
    {
    	if (resultData[0]["next_value"]==2)
		{
    		document.getElementById("prag").innerHTML ="<li ><a>prev</a></li>\r\n"+
			"<li ><a>"+(resultData[0]["next_value"]-1)+"</a></li>\r\n"+
			"<li ><a>next</a></li>";
		}
    	if (resultData[0]["next_value"]>2)
    		{
    			
    		document.getElementById("prag").innerHTML ="<li > <a href='"+"browse_title_result.html?title="+resultData[0]["title"]+"&page="+(resultData[0]["next_value"]-2)+"'>prev</a></li>\r\n"+
			"<li ><a>"+(resultData[0]["next_value"]-1)+"</a></li>\r\n"+
			"<li ><a>next</a></li>\r\n";
    		}
    }
    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 1; i < resultData.length; i++) {
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
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let title = getParameterByName('title');
let page = getParameterByName('page');
console.log(title)
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/browse_title_result?title="+title+"&"+"page="+page, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});