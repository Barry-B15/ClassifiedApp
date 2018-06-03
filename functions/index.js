const functions = require('firebase-functions');

// we saw this constant earlier during the set up config, we insatlled the nodejs lib called request promise. it helps us to make http requests
const request =   require('request-promise') //this is the request we send to the server

// create the func itself, when someone post something this func will triger
exports.indexPostsToElastic = functions.database.ref('/posts/{post_id}')  // this will tell fb to export the func to the server
	.onWrite(event => {
		let postData = event.data.val();  // this will generate all the json properties for the post when a user posts something
		let post_id = event.params.post_id;
		
		console.log('Indexing post:', postData);
		
		let elasticSearchConfig = functions.config().elasticsearch;
		let elasticSearchUrl = elasticSearchConfig.url + 'posts/post/' + post_id;
		let elasticSearchMethod = postData ? 'POST' : 'DELETE';
		
		// def a new func
		let elasticSearchRequest = {
			method: elasticSearchMethod,
			url: elasticSearchUrl,  
			auth: {
					username: elasticSearchConfig.username,
					password: elasticSearchConfig.password,
				},
			body: postData,
			json: true           // for type of data
		};
		
		return request(elasticSearchRequest).then(response => {
			console.log("ElasticSearch response", response);
		});
	});  
		
	