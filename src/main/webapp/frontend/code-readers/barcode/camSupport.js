var doExit = false;

var barcodeCam = {
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

        barcodeCam.loadingMessage.classList.add("loading");
        if (barcodeCam.video.readyState === barcodeCam.video.HAVE_ENOUGH_DATA) {
            barcodeCam.loadingMessage.hidden = true;
            barcodeCam.canvasElement.hidden = false;

            barcodeCam.canvasElement.height = barcodeCam.video.videoHeight;
            barcodeCam.canvasElement.width = barcodeCam.video.videoWidth;
            barcodeCam.canvas.drawImage(barcodeCam.video, 0, 0, barcodeCam.canvasElement.width, barcodeCam.canvasElement.height);
            var imageData = barcodeCam.canvas.getImageData(0, 0, barcodeCam.canvasElement.width, barcodeCam.canvasElement.height);

            javascriptBarcodeReader( imageData, {
                        barcode: 'code-2of5',
                        type: 'industrial', //optional type
                    })
            .then(code => {
                console.log(code)
            })
            .catch(err => {
                console.log(err);
                doExit = true;
            })

//			if (code) {
//                barcodeCam.drawLine(code.location.topLeftCorner, code.location.topRightCorner, "#FF3B58");
//                barcodeCam.drawLine(code.location.topRightCorner, code.location.bottomRightCorner, "#FF3B58");
//                barcodeCam.drawLine(code.location.bottomRightCorner, code.location.bottomLeftCorner, "#FF3B58");
//                barcodeCam.drawLine(code.location.bottomLeftCorner, code.location.topLeftCorner, "#FF3B58");
//                console.log("Code: ", code);
//                if (barcodeCam.prevCodeData !== code.data) {
//                    barcodeCam.prevCodeData = code.data;
//                    barcodeCam.canvasElement.parentElement.$server.onClientCodeRead(code.data);
//                }
//            }
        }

        requestAnimationFrame(barcodeCam.tick);
    },
    init: function () {
        doExit = false;

        this.video = document.createElement("video");
        this.canvasElement = document.getElementById("barcodeCamCanvas");
        this.canvas = this.canvasElement.getContext("2d");
        this.loadingMessage = document.getElementById("barcodeCamLoadingMessage");
        // Use facingMode: environment to attempt to get the front camera on phones
        navigator.mediaDevices.getUserMedia({video: {facingMode: "environment"}}).then(function (stream) {
            barcodeCam.video.srcObject = stream;
            barcodeCam.video.setAttribute("playsinline", true); // required to tell iOS safari we don't want fullscreen
            barcodeCam.video.play();

            requestAnimationFrame(barcodeCam.tick);
        });
    },
    reset: function () {
        this.prevCodeData = null;
    },
    stop: function () {
        let tracks = barcodeCam.video.srcObject.getTracks();

		tracks.forEach(function(track) {
			track.stop();
		});
		doExit = true;
    }
};

