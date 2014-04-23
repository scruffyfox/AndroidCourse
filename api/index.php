<?PHP

date_default_timezone_set("GMT");
error_reporting(0);

$type = $_GET['type'];

if (!$type){
	
	
	echo file_get_contents("readme.html");
}

if ($type == "topics"){
		
	$topics = load_topics(null);
	
	$response["topics"] = $topics;
	
	echo json_encode($response);
	
}

// Stories
if ($type == "stories"){
	
	$topic = $_GET["topic"];

	$stories = load_stories($topic);
	
	if($stories){
		$response["stories"] = $stories;
		$response["topic"] = load_topics($topic);
	}
	
	echo json_encode($response);
}

function load_topics($id){

	$topics = json_decode(file_get_contents("topics.json"), true);
	
	if ($id) {
	
		foreach($topics as $topic){
		
			if($topic["id"] == $id){
				return $topic;
			}
			
		}
		
	} else {
		return $topics;
	}	
}

function load_stories($topic)
{
	if($topic == "headlines"){
		$url = "http://feeds.bbci.co.uk/news/rss.xml";
	} else {
		$url = "http://feeds.bbci.co.uk/news/$topic/rss.xml";
	}
	
	$data = load_data($url);
	$xml = simplexml_load_string($data);
	
	$items = array();
	
	foreach($xml->channel->item as $object){
			
		$item['title'] = (string)$object->title;
		$item['description'] = (string)$object->description;
		$item['link'] = (string)$object->link;
		$item['published'] = strtotime((string)$object->pubDate);
		
		$namespaces = $object->getNamespaces(true);
		$media = $object->children($namespaces["media"]);
		
		foreach($media->thumbnail as $thumbnail){

			// Thumbnail in XML namespace
			$thumbnailAttributes = $thumbnail->attributes();
			
			// Return biggest thumbnail
			if ($thumbnailAttributes->width > 140){				
				$item['thumbnail'] = (string)$thumbnailAttributes->url;
			}
		}
		
		array_push($items, $item);
	}
	
	return $items;
}





/* --------------- */
/* Helper methods  */
/* --------------- */
function load_data($url){
	
	$timeout = 5;
	
	$curl = curl_init();
	
	curl_setopt($curl, CURLOPT_URL, $url);
	curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($curl, CURLOPT_CONNECTTIMEOUT, $timeout);
	
	$data = curl_exec($curl);
	
	return $data;
		
}

?>