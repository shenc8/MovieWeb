function handleGenresResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");
    
    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let genresTableBodyElement = jQuery("#genres_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            "<a href=\"browse_genres_result.html?genre="+resultData[i]["genre"]+"&page=1\">"+resultData[i]["genre"]+"</a>"
//            '<form ACTION ="api/browse_genres_result.html?genre='+resultData[i]["genre"]+' METHOD="GET">'+
//            // Add a link to single-star.html with id passed with GET url parameter
//            '<input type="submit" name="genre" value="'+resultData[i]["genre"]+'">'
//              // display star_name for the link text
//            + '</form>'
            +"</th>";
        rowHTML += "</tr>";
      
        // Append the row created to the table body, which will refresh the page
        genresTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/browse_genres", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleGenresResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});