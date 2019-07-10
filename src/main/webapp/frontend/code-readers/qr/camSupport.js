var doExit = false;

var jsQRCam = {
    prevCodeData: null,
    drawLine: function (begin, end, color) {
        this.canvas.beginPath();
        this.canvas.moveTo(begin.x, begin.y);
        this.canvas.lineTo(end.x, end.y);
        this.canvas.lineWidth = 4;
        this.canvas.strokeStyle = color;
        this.canvas.stroke();
    },

    tick: function () {
        if (doExit) {
            return;
        }
        jsQRCam.loadingMessage.classList.add("loading");
        if (jsQRCam.video.readyState === jsQRCam.video.HAVE_ENOUGH_DATA) {
            jsQRCam.loadingMessage.hidden = true;
            jsQRCam.canvasElement.hidden = false;

            jsQRCam.canvasElement.height = jsQRCam.video.videoHeight;
            jsQRCam.canvasElement.width = jsQRCam.video.videoWidth;
            jsQRCam.canvas.drawImage(jsQRCam.video, 0, 0, jsQRCam.canvasElement.width, jsQRCam.canvasElement.height);
            var imageData = jsQRCam.canvas.getImageData(0, 0, jsQRCam.canvasElement.width, jsQRCam.canvasElement.height);
            var code = jsQR(imageData.data, imageData.width, imageData.height);
            if (code) {
                jsQRCam.drawLine(code.location.topLeftCorner, code.location.topRightCorner, "#FF3B58");
                jsQRCam.drawLine(code.location.topRightCorner, code.location.bottomRightCorner, "#FF3B58");
                jsQRCam.drawLine(code.location.bottomRightCorner, code.location.bottomLeftCorner, "#FF3B58");
                jsQRCam.drawLine(code.location.bottomLeftCorner, code.location.topLeftCorner, "#FF3B58");
                console.debug("Code: ", code);
                if (jsQRCam.prevCodeData !== code.data) {
                    jsQRCam.prevCodeData = code.data;
                    jsQRCam.canvasElement.parentElement.$server.onClientCodeRead(code.data);
                }
            }
        }
        requestAnimationFrame(jsQRCam.tick);
    },
    init: function () {
        doExit = false;
        this.video = document.createElement("video");
        this.canvasElement = document.getElementById("jsQRCamCanvas");
        this.canvas = this.canvasElement.getContext("2d");
        this.loadingMessage = document.getElementById("jsQRCamLoadingMessage");
        // Use facingMode: environment to attempt to get the front camera on phones
        navigator.mediaDevices.getUserMedia({video: {facingMode: "environment"}}).then(function (stream) {
            jsQRCam.video.srcObject = stream;
            jsQRCam.video.setAttribute("playsinline", true); // required to tell iOS safari we don't want fullscreen
            jsQRCam.video.play();
            requestAnimationFrame(jsQRCam.tick);
        });
    },
    reset: function () {
        this.prevCodeData = null;
    },
    stop: function () {
        let tracks = jsQRCam.video.srcObject.getTracks();

		tracks.forEach(function(track) {
			track.stop();
		});
		doExit = true;
    }
};

