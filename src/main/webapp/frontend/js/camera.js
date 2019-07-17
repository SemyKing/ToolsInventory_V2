
var camera = {
	canvas: null,
	canvasContext: null,
	stream: null,
	target: {
        type: String,
        value: ""
	},

	drawFrameToCanvas: function() {
        if (camera.video.readyState === camera.video.HAVE_ENOUGH_DATA) {

            camera.canvas.height = camera.video.videoHeight;
            camera.canvas.width = camera.video.videoWidth;
            camera.canvasContext.drawImage(camera.video, 0, 0, camera.video.videoWidth, camera.video.videoHeight);
        }
        requestAnimationFrame(camera.drawFrameToCanvas);
	},

    init: function () {
		if (this.stream != null) {
            camera.stop;
        }

        this.video = document.createElement("video");
        this.video.style.width="100%";
        camera.canvas = document.getElementById("canvas");
        camera.canvas.hidden = false;
        camera.canvasContext = camera.canvas.getContext("2d");

        // Use facingMode: environment to attempt to get the front camera on phones
        navigator.mediaDevices.getUserMedia(
           {   video: {facingMode: "environment"},
               audio: false
           }).then(function (stream) {
	            camera.stream = stream;
	            camera.video.srcObject = stream;
	            camera.video.setAttribute("playsinline", true); // required to tell iOS safari we don't want fullscreen
	            camera.video.play();
	            requestAnimationFrame(camera.drawFrameToCanvas);
        });
    },


    takePicture: function() {
		camera.canvas.height = camera.video.videoHeight;
		camera.canvas.width = camera.video.videoWidth;
        camera.canvasContext.drawImage(camera.video, 0, 0, camera.video.videoWidth, camera.video.videoHeight);

        let blob = camera.canvas.toBlob(b => {
            camera.saveToServer(b);
        },'image/jpeg',0.95)
    },


    saveToServer: function(data) {
        let formData = new FormData();
        formData.append("data", data);
        fetch(this.target, {
            method: "post",
            body: formData
        }).then(response => console.log(response));
    },


    stop: function() {
        if (camera.stream != null) {
            camera.stream.getTracks().forEach( track => {
                console.log("---STOP TRACK: " + track);
                track.stop();
            });
        }
    }
};