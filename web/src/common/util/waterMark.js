let isIE9 = document.all && document.addEventListener && !window.atob;
let timeoutId = null;
let setIntervalId = null;
let waterMark = {
  waterMarkModel: {
    id: "",
    waterMaskBoxId: "waterMaskContainer",
    frontX: "",
    frontY: "",
    frontRows: "",
    frontCols: "",
    maskTxt: "",
    color: "#000000",
    width: 150,
    height: 50,
    fontSize: "16px",
    frontFont: "微软雅黑",
    frontBackgroundAlpha: 0,
    frontTxtAlpha: 0.1,
    angle: 15,
    frontXSpace: 15,
    frontYSpace: 15,
    backgroundColor: "#000000",
    resizeable: true,
    addTime: false,
    setIntervalTime: "60000",
    dateFormatter: "YYYY-MM-DD hh:mm",
    zIndex: 99999
  },
  init: function (options) {
    if (options === void 0) {
      options = {};
    }
    waterMark.removeWaterMask();
    waterMark.initWaterMarkModel(options);
    waterMark.createWaterMark();
    waterMark.initEvent();
  },
  initEvent: function () {
    if (waterMark.waterMarkModel.resizeable) {
      window.addEventListener("resize", waterMark.resize);
    }
  },
  resize: function () {
    waterMark.removeWaterMask();
    waterMark.createWaterMark();
  },
  initWaterMarkModel: function (options) {
    let id = options.id || waterMark.waterMarkModel.id;
    let relation = id
      ? document.getElementById(id)
      : document.getElementsByTagName("body")[0];
    let relationTop = relation.offsetTop;
    let relationHeight = relation.offsetHeight;
    let relationWidth = relation.offsetWidth;
    let relationLeft = relation.offsetLeft;
    let itemHeight = options.height || waterMark.waterMarkModel.height;
    let itemWidth = options.width || waterMark.waterMarkModel.width;
    let frontRows = relationHeight / itemHeight;
    let frontCols = relationWidth / itemWidth;
    waterMark.waterMarkModel = Object.assign(
      Object.assign(waterMark.waterMarkModel, {
        frontX: relationLeft,
        frontY: relationTop,
        frontRows:
          frontRows > parseInt(frontRows)
            ? parseInt(frontRows) + 1
            : parseInt(frontRows),
        frontCols:
          frontCols > parseInt(frontCols)
            ? parseInt(frontCols) + 1
            : parseInt(frontCols),
      }),
      options
    );
  },
  curentTime: function () {
    let now = new Date();
    let year = now.getFullYear(); // 年
    let month = now.getMonth() + 1; // 月
    let day = now.getDate(); // 日
    let hh = now.getHours(); // 时
    let mm = now.getMinutes(); // 分
    let ss = now.getSeconds();
    if (month < 10) month = "0" + month;
    if (day < 10) day = "0" + day;
    if (hh < 10) hh = "0" + hh;
    if (mm < 10) mm = "0" + mm;
    if (ss < 10) ss = "0" + ss;
    let dateFormatter = waterMark.waterMarkModel.dateFormatter;
    let dateTime = dateFormatter.replace('YYYY', year).replace('MM', month).replace('DD', day).replace('hh', hh).replace('mm', mm).replace('ss', ss);
    return {
      dateTime: dateTime,
      ss: ss
    };
  },
  getMaskTxt: function () {
    let maskTxt = waterMark.waterMarkModel.maskTxt;
    if (waterMark.waterMarkModel.addTime) {
      let currentTime = waterMark.curentTime();
      maskTxt = maskTxt + ' ' + currentTime.dateTime;
      if (!timeoutId) {
        timeoutId = setTimeout(function () {
          waterMark.updateMaskTxt();
          if (!setIntervalId) {
            setIntervalId = setInterval(waterMark.updateMaskTxt, waterMark.waterMarkModel.setIntervalTime);
          }
        }, (60 - currentTime.ss) * 1000);
      }
    }
    return maskTxt;
  },
  updateMaskTxt: function () {
    waterMark.resize();
  },
  createWaterMark: function () {
    let oTemp = document.createElement("div");
    let frontXSpace = waterMark.waterMarkModel.frontXSpace;
    let frontYSpace = waterMark.waterMarkModel.frontYSpace;
    let frontRows = waterMark.waterMarkModel.frontRows;
    let frontCols = waterMark.waterMarkModel.frontCols;
    let frontX = waterMark.waterMarkModel.frontX;
    let frontY = waterMark.waterMarkModel.frontY;
    let maskTxt = waterMark.getMaskTxt();
    let frontTxtAlpha = waterMark.waterMarkModel.frontTxtAlpha;
    let fontSize = waterMark.waterMarkModel.fontSize;
    let frontFont = waterMark.waterMarkModel.frontFont;
    let width = waterMark.waterMarkModel.width;
    let height = waterMark.waterMarkModel.height;
    let angle = waterMark.waterMarkModel.angle;
    let color = waterMark.waterMarkModel.color;
    let zIndex = waterMark.waterMarkModel.zIndex;

    let maxWidth =
      Math.max(
        document.body.scrollWidth,
        document.documentElement.scrollWidth
      ) - 20;
    let maxHeight =
      Math.max(
        document.body.scrollHeight,
        document.documentElement.scrollHeight
      ) - 20;

    if (
      frontCols == 0 ||
      parseInt(
        frontX + width * frontCols + frontXSpace * (frontCols - 1)
      ) > maxWidth
    ) {
      frontCols = parseInt(
        (frontXSpace + maxWidth - frontX) / (width + frontXSpace)
      );
      frontXSpace = parseInt(
        (maxWidth - frontX - width * frontCols) / (frontCols - 1)
      );
      if (!frontXSpace) {
        frontXSpace = 0;
      }
    }
    if (
      frontRows == 0 ||
      parseInt(
        frontY + height * frontRows + frontYSpace * (frontRows - 1)
      ) > maxHeight
    ) {
      frontRows = parseInt(
        (frontYSpace + maxHeight - frontY) / (height + frontYSpace)
      );
      frontYSpace = parseInt(
        (maxHeight - frontY - height * frontRows) / (frontRows - 1)
      );
      if (!frontYSpace) {
        frontYSpace = 0;
      }
    }
    let x;
    let y;
    for (let i = 0; i < frontRows; i++) {
      y = frontY + (frontYSpace + height) * i;
      for (let j = 0; j < frontCols; j++) {
        x = frontX + (width + frontXSpace) * j;
        let maskDiv = document.createElement("div");
        let m = waterMark.getRotation(-angle);
        maskDiv.id = "mask_div" + i + j;
        maskDiv.appendChild(document.createTextNode(maskTxt));
        maskDiv.style.webkitTransform = "rotate(-" + angle + "deg)";
        maskDiv.style.MozTransform = "rotate(-" + angle + "deg)";
        maskDiv.style.msTransform = "rotate(-" + angle + "deg)";
        maskDiv.style.OTransform = "rotate(-" + angle + "deg)";
        maskDiv.style.transform = "rotate(-" + angle + "deg)";
        maskDiv.style.visibility = "";
        maskDiv.style.position = "fixed";
        maskDiv.style.left = x + "px";
        maskDiv.style.top = y + "px";
        maskDiv.style.overflow = "hidden";
        // mask_div.style.border="solid #eee 1px";
        maskDiv.style.opacity = frontTxtAlpha;
        if (isIE9) {
          maskDiv.style.filter =
            "progid:DXImageTransform.Microsoft.Alpha(opacity=" +
            frontTxtAlpha * 100 +
            ")";
        } else {
          maskDiv.style.filter =
            "progid:DXImageTransform.Microsoft.Alpha(opacity=" +
            frontTxtAlpha * 100 +
            ") progid:DXImageTransform.Microsoft.Matrix(sizingMethod='auto expand', M11=" +
            m[0] +
            ", M12=" +
            m[1] +
            ", M21=" +
            m[2] +
            ", M22=" +
            m[3] +
            ")";
        }
        maskDiv.style.fontSize = fontSize;
        maskDiv.style.fontFamily = frontFont;
        maskDiv.style.color = color;
        maskDiv.style.textAlign = "center";
        maskDiv.style.width = width + "px";
        maskDiv.style.height = height + "px";
        maskDiv.style.display = "block";
        maskDiv.style.zIndex = zIndex;
        maskDiv.style.pointerEvents = "none";
        oTemp.id = waterMark.waterMarkModel.waterMaskBoxId;
        oTemp.appendChild(maskDiv);
      }
    }
    document.body.appendChild(oTemp);
  },
  getRotation: function (deg) {
    let deg2Rad = (Math.PI * 2) / 360;
    let rad = deg * deg2Rad;
    let costheta = Math.cos(rad);
    let sintheta = Math.sin(rad);
    let m11 = costheta;
    let m12 = -sintheta;
    let m21 = sintheta;
    let m22 = costheta;
    return [m11, m12, m21, m22];
  },
  removeWaterMask: function () {
    setIntervalId && clearInterval(setIntervalId)
    setIntervalId = null
    timeoutId && clearTimeout(timeoutId)
    timeoutId = null
    let parentEl = document.getElementById(
      waterMark.waterMarkModel.waterMaskBoxId
    );
    if (parentEl) {
      parentEl.parentNode.removeChild(parentEl);
    }
  }
};
export default waterMark;
