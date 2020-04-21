import {PolymerElement, html} from '@polymer/polymer';

class CameraElement extends PolymerElement {
	static get template() {
        return html`
            <div id=content part=content class="camera_element"></div>
            <canvas id=canvas hidden></canvas>`;
    }

	static get is() {
        return 'camera-element';
    }

    static get properties() {
        return {
            previewOptions: {
                type:Object,
                value: null
            },
            recordingOptions: {
                type:Object,
                value: null
            },
            imageData: {
                type: Object,
                value:null
            },
            recordingData: {
                type: Object,
                value: null
            },
            target: {
                type:String,
                value:""
            }
        }
    }

	_clearContents() {
        let el = this.$.content;
        while (el.hasChildNodes()) {
        el.removeChild(el.lastChild);
        }
    }

    _createVideo() {
        this._clearContents();
        let vid = document.createElement("video");
        vid.id="video";
        vid.style.width="100%";
        let el = this.$.content;
        el.appendChild(vid);
        return vid;
    }

    _createImage() {
        this._clearContents();
        let img = document.createElement("img");
        img.id="img";
        img.style.width="100%";
        let el = this.$.content;
        el.appendChild(img);
        return img;
    }

    _stopStream(stream) {
        if(stream != null) {
            stream.getTracks().forEach( track => {
                track.stop();
            });
        }
    }

	saveToServer(data) {
        let formData = new FormData();
        formData.append("data", data);

        fetch(this.target, {
            method: "post",
            body: formData });
    }

    takePicture() {
        let vid = this.root.querySelector("#video");

        if (vid == null) {
            return;
        }

        let canvas =  this.$.canvas;
        let context = canvas.getContext('2d');
        canvas.height = vid.videoHeight;
        canvas.width = vid.videoWidth;
        context.drawImage(vid, 0, 0, vid.videoWidth, vid.videoHeight);

        let blob = this.$.canvas.toBlob(b => {
            this.imageData = b;
            this.saveToServer(b);
        }, 'image/jpeg',0.95)
    }

    showPicture() {
        let img = this._createImage();
        img.src = URL.createObjectURL(this.imageData);
    }

    showPreview() {
        let video = this._createVideo();

        if (this.stream != null) {
            this._stopStream(this.stream);
        }

        if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {

            const constraints = {
                audio: false,
                video: { facingMode: "environment" }
            };

            navigator.mediaDevices.getUserMedia(constraints).then(stream => {
                this.stream = stream;
                video.srcObject = this.stream;
                video.play();
            });
        }
    }

    stopCamera() {
        this._stopStream(this.stream);
    }
}

customElements.define(CameraElement.is, CameraElement);