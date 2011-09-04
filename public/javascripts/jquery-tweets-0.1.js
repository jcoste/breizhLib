/*
* Author:      Marco Kuiper (http://www.marcofolio.net/)
*/

$(document).ready(function()
{
	/**
	* Set the size for each page to load
	*/
	var pageSize = 10;
	
	/**
	* Username to load the timeline from
	*/
	var username = 'breizhjug';
	
	/**
	* Variable for the current page
	*/
	var currentPage = 1;
	
	// Appends the new tweet to the UI
	var appendTweet = function(tweet, id) {
		$("<p />")
			.html(tweet.replace(/(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig, '<a href="$&">$&</a> ')
                       .replace(/@(.*?)(\s|\(|\)|$)/g, '<a href="http://twitter.com/$1">@$1 </a>$2'))
            .append($("<a />")
					.attr("href", "http://twitter.com/" + username + "/status/" + id)
					.attr("title", "Go to Twitter status")
                    .attr("target", "_blank")
					.append($("<img />")
						.attr("src", "public/images/link.png")
					)
			)
		.appendTo($("#tweets"));
	};
	
	// Loads the next tweets
	var loadTweets = function() {
		var url = "http://twitter.com/status/user_timeline/"
				+ username + ".json?count="+pageSize+"&page="+currentPage+"&callback=?";
				
		$.getJSON(url,function(data) {
			$.each(data, function(i, post) {
				appendTweet(post.text, post.id);
			});
			
			// We're done loading the tweets, so hide the overlay and update the UI
			$("#overlay").fadeOut();
			$("#pageCount").html(currentPage);
			$("#tweetCount").html(currentPage * pageSize);
		});
		
	};
	
	// First time, directly load the tweets
	loadTweets();
	
	// Append a scroll event handler to the container
	$("#tweets").scroll(function() {
		// We check if we're at the bottom of the scrollcontainer
		if ($(this)[0].scrollHeight - $(this).scrollTop() == $(this).outerHeight()) {
			// If we're at the bottom, show the overlay and retrieve the next page
			currentPage++;
			
			if(currentPage > 10) {
				alert('We should not spam the Twitter API with calls. I hope you get the idea!');
				return false;
			}
			
			$("#overlay").fadeIn();
			loadTweets();
		}
	});
	
});