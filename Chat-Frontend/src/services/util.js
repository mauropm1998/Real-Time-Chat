const profile = "dev";

export function server(websocket = false) {
    return profile === "dev" ? `http://localhost:8080/${websocket ? 'ws' : 'api/v1'}`: `https://chat.appteste.info/${websocket ? 'ws' : 'api/v1'}`;
}

export function calculateFileSize(size){
	let precision = 1;
	let factor = Math.pow(10, precision);

	size = Math.round(size / 1024);

	if(size < 1000){
		return size + " KB";
	}
	else{
		size = Number.parseFloat(size / 1024);
		if(size < 1000){
			return (Math.round(size * factor) / factor) + " MB";
		}
		else{
			size = Number.parseFloat(size / 1024);
			return (Math.round(size * factor) / factor) + " GB";
		}
	}
}
