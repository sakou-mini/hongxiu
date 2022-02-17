layui.use('layer', function () {
  var layer = layui.layer
});
layui.use('element', function () {
  var element = layui.element;
});
var pathname = window.location.pathname;
var offsetWid = document.documentElement.clientWidth;
var only = true;
var isSm = false;
$.get({
  url: window.ioApi.backOffice.getBackOfficeMsg,
  success: function (res) {
    console.log(res);
    $("#loginName").text(res.accountName);
    localStorage.setItem("loginMsg", JSON.stringify(res));
  },
  error: function () {
    layer.open({
      title: '错误信息'
      , content: '服务器错误'
    });
  }
});
window.onresize = function () {
  watchChangeSize();
};
/**
 * @method watchChangeSize 判断当前浏览器宽度是是否合适，过低的浏览器宽度弹出警告
 * @callback 弹出浏览器宽度过低提示模态框
 * */
function watchChangeSize () {
  var nowOffsetWid = document.documentElement.clientWidth;
  if (nowOffsetWid < offsetWid) {
    isSm = true
  } else {
    isSm = false
  }
  offsetWid = nowOffsetWid;
  if (offsetWid <= 830 && offsetWid >= 500 && only && isSm) {
    only = false;
    layer.open({
      title: '警告'
      , content: '过低的浏览器宽度会使数据展示不全',
      btn: ["确认"],
      btn1: function (index, layero) {
        only = true;
        layer.closeAll();
      }, cancel: function () {
        only = true;
      }
    });
  }
}
/**
 * @method jumpDetailsUrl 携带一个参数的路由跳转
 * @param {string} pathName 跳转路由的名字
 * @param {string} key 携带参数的键名
 * @param {string} data 携带的参数
 * */
function jumpDetailsUrl (pathName, key, data) {
  for (var i = 0; i < window.pathUrl.length; i++) {
    if (window.pathUrl[i].name === pathName) {
      window.location.href = window.pathUrl[i].url + "?" + key + "=" + data;
    }
  }
}

function jumpEncodeUrl (pathName, data) {
  for (var i = 0; i < window.pathUrl.length; i++) {
    if (window.pathUrl[i].name === pathName) {
      window.location.href = window.pathUrl[i].url + "?" + urlEncode(data)
    }
  }
}

/**
 * @method jumpUrl 跳转路由方法
 * @param {string} pathName 跳转路由的名字
 * */
function jumpUrl (pathName) {
  for (var i = 0; i < window.pathUrl.length; i++) {
    if (pathName === window.pathUrl[i].name) {
      window.location.href = window.pathUrl[i].url
    }
  }

}




/**
 * @method formatDate 时间格式转化 yyy-mmm-ddd
 * @param {string} intDate 时间戳
 * @return 返回格式化后的时间 yyy-mmm-ddd
 * */
function formatDate (intDate) {
  var time = new Date(intDate);
  var year = time.getFullYear();
  var month = time.getMonth() + 1;
  var date = time.getDate();
  var hour = time.getHours();
  var minute = time.getMinutes();
  var second = time.getSeconds();
  return year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second;
}

function formatSpecificDate (intDate) {
  var time = new Date(intDate);
  var year = time.getFullYear();
  var month = time.getMonth() + 1;
  var date = time.getDate();
  return year + "-" + month + "-" + date
}

/**
 * @method guessFilter 竞猜信息过滤器
 * @param {Array} list 竞猜对象数组
 * @param {boolean} isLotter 是否需要判断是否开奖
 * @return {Array} 格式化时间、转化后的类型和状态的竞猜信息数组
 * */
function guessFilter (list, isLotter) {
  for (var i = 0; i < list.length; i++) {
    list[i].profit = Math.floor(list[i].totalCoin * 0.1);
    list[i].showStartTime = formatDate(list[i].showStartTime);
    list[i].showEndTime = formatDate(list[i].showEndTime);
    list[i].wagerStartTime = formatDate(list[i].wagerStartTime);
    list[i].wagerEndTime = formatDate(list[i].wagerEndTime);
    switch (list[i].guessType) {
      case "POLITICS":
        list[i].guessType = "政治";
        break;
      case "SPORTS":
        list[i].guessType = "体育";
        break;
      case "ELECTRONIC":
        list[i].guessType = "电竞";
        break;
      case "OTHER":
        list[i].guessType = "其他";
        break
    }
    if (isLotter == null) {
      switch (list[i].state) {
        case "NOT_WAGER":
          list[i].state = "未下注";
          break;
        case "WAGER":
          list[i].state = "下注中";
          break;
        case "WAGER_OVER":
          list[i].state = "停止下注";
          break;
        case "LOTTERY":
          list[i].state = "已开奖";
          break;
      }
    }
    else {
      if (list[i].state == "LOTTERY") {
        list[i].state = "已开奖";
      }
      else {
        list[i].state = "未开奖"
      }
    }
  }
  return list;
}


/**
 * @method upLoadVerification 验证新增竞猜是否合法
 * @param {Object} upObj 上传的竞猜对象
 * @code {int} code 是否开启验证所有时间大于当前时间 0开启 1不开启
 * @return {Object} 验证是否通过
 * code验证状态码(200验证通过，405竞猜选项没填完，400竞猜选项、竞猜类型外有字段未填、403竞猜选项有重复、408有时间小于当前时间、411-413上传时间非法)
 * msg 验证的提示信息
 * nullKey为填写的key名
 * */
function upLoadVerification (upObj, code) {
  var backObj = {};
  console.log(upObj.itemList.length);
  for (var i = 0; i < upObj.itemList.length; i++) {
    if (upObj.itemList[i] === "") {
      backObj.code = 405;
      backObj.msg = "竞猜选项没有填完";
      backObj.nullKey = "itemList";
      return backObj;
    }
  }
  for (var key in upObj) {
    if (key !== "itemList" && key !== "guessType") {
      if (upObj[key] === "" || upObj[key] === undefined || upObj[key] === null || !upObj[key]) {
        console.log(upObj);
        backObj.code = 400;
        backObj.msg = "有字段为空";
        backObj.nullKey = key;
        return backObj;
      }
    }
  }
  if (isRepeat(upObj.itemList)) {
    backObj.code = 403;
    backObj.msg = "选项有重复";
    return backObj;
  }
  var nowTime = new Date().getTime();
  if (code === 0) {
    if (upObj.showEndTime < nowTime ||
      upObj.showStartTime < nowTime ||
      upObj.wagerEndTime < nowTime ||
      upObj.wagerStartTime < nowTime) {
      backObj.code = 408;
      backObj.msg = "任意时间不得小于当前时间";
      return backObj;
    }
  }
  if (upObj.showEndTime <= upObj.showStartTime) {
    backObj.code = 411;
    backObj.msg = "展示结束时间应该在展示开始时间之前";
    return backObj;
  }
  if (upObj.wagerEndTime <= upObj.wagerStartTime) {
    backObj.code = 412;
    backObj.msg = "下注结束时间应该在下注开始时间之前";
    return backObj;
  }
  if (upObj.showStartTime >= upObj.wagerStartTime || upObj.showEndTime <= upObj.wagerEndTime) {
    backObj.code = 413;
    backObj.msg = "下注时间段应该在展示时间段之中";
    return backObj;
  }
  backObj.code = 200;
  backObj.msg = "通过验证";
  return backObj
}


$("#resetData").on("click", function () {
  location.reload();
});


/**
 * @method isRepeat 验证数组是否有重复
 * @param {Array} arr 需要验证是否重复
 * @return {boolean} 数组是否有重复项
 * */
function isRepeat (arr) {
  var hash = {};
  for (var i in arr) {
    if (hash[arr[i]])
      return true;
    hash[arr[i]] = true;
  }
  return false;
}

/**
 * @method navItemPrototype 头部导航栏对象原型
 * @param {string} name 导航li标签的name属性，用于路由跳转
 * @param {string} url 用于判断当前页面路由是否为当前标签对应的地址，用于修改样式
 * @param {string} displayName 用于显示头部导航的名字
 */
function navItemPrototype (name, url, displayName) {
  this.itemEle = document.createElement("li");
  this.itemChild = document.createElement("a");
  this.init = function () {
    this.itemChild.href = "javascript:void(0);";
    this.itemChild.innerText = displayName;
    var dataUrlArr = url.split("/");
    var nowPathArr = pathname.split("/");
    if (dataUrlArr[2] === nowPathArr[2]) {
      this.itemEle.setAttribute("class", "layui-nav-item toItemChange layui-this")
    }
    else {
      this.itemEle.setAttribute("class", "layui-nav-item toItemChange")
    }
    this.itemEle.setAttribute("data-path", name);
    this.itemEle.appendChild(this.itemChild);
    var liEleLast = document.getElementsByClassName("userMsg")[0];
    document.getElementsByClassName("layui-nav")[0].insertBefore(this.itemEle, liEleLast);
  };
  this.init();
}
/**
 * @method sidebarItemPrototype 侧边栏对象原型
 * @param {string} name 侧边栏li标签的name属性，用于路由跳转
 * @param {string} url 用于判断当前页面路由是否为当前标签对应的地址，用于修改样式
 * @param {string} displayName 用于显示侧边栏的名字
 * */
function sidebarItemPrototype (name, url, displayName) {
  this.itemEle = document.createElement("li");
  this.itemChild = document.createElement("a");
  this.init = function () {
    this.itemChild.href = "javascript:void(0);";
    this.itemChild.innerText = displayName;
    if (pathname === url) {
      this.itemEle.setAttribute("class", "layui-nav-item toItemChange layui-this")
    }
    else {
      this.itemEle.setAttribute("class", "layui-nav-item toItemChange")
    }
    this.itemEle.setAttribute("data-path", name);
    this.itemEle.appendChild(this.itemChild);
    var sidebarBox = document.getElementById("sidebar");
    sidebarBox.setAttribute("lay-shrink","all")
    sidebarBox.appendChild(this.itemEle)
  };
  this.init();
}

function sidebarChildPrototype (childName, child) {
  this.itemEle = document.createElement("li");
  this.itemChild = document.createElement("a");
  this.itemdl = document.createElement("dl");
  this.init = function () {
    this.itemEle.setAttribute("class", "layui-nav-item");
    this.itemChild.href = "javascript:;";
    this.itemChild.innerHTML = childName;
    this.itemdl.setAttribute("class", "layui-nav-child");
    for (var i = 0; i < child.length; i++) {
      var dd = document.createElement("dd");
      var a = document.createElement("a");
      if (pathname === child[i].url) {
        a.setAttribute("class", "toItemChange layui-this sidebarChildStyle")
          this.itemEle.setAttribute("class", "layui-nav-item layui-nav-itemed");
      }
      else {
        a.setAttribute("class", "toItemChange sidebarChildStyle")
      }
      a.setAttribute("data-path", child[i].name);
      a.href = "javascript:void(0);";
      a.innerHTML = child[i].displayName;
      dd.appendChild(a);
      this.itemdl.appendChild(dd);
    }
    this.itemEle.appendChild(this.itemChild);
    this.itemEle.appendChild(this.itemdl);
    var sidebarBox = document.getElementById("sidebar");
    sidebarBox.appendChild(this.itemEle)
  };
  this.init();
}


function generateNav (config) {
  var data = config;
  for (var i = 0; i < data.length; i++) {
    navItemPrototype(data[i].name, data[i].url, data[i].displayName);
    var dataUrlArr = data[i].url.split("/");
    var nowPathArr = pathname.split("/");
    if (dataUrlArr[2] === nowPathArr[2]) {
      for (var x = 0; x < data[i].sidebar.length; x++) {
        console.log(data[i].sidebar[x].child);
        if (data[i].sidebar[x].child === undefined || data[i].sidebar[x].child === null || data[i].sidebar[x].child === NaN || data[i].sidebar[x].child === "") {
          sidebarItemPrototype(data[i].sidebar[x].name, data[i].sidebar[x].url, data[i].sidebar[x].displayName)
        }
        else {
          sidebarChildPrototype(data[i].sidebar[x].childName, data[i].sidebar[x].child)
        }

      }
    }
  }
}



generateNav(JSON.parse(sessionStorage.getItem("nav")));
$(".toItemChange").on("click", function () {
  var pathName = this.getAttribute("data-path");
  var pathArr = window.pathUrl;
  for (var i = 0; i < pathArr.length; i++) {
    if (pathName === pathArr[i].name) {
      window.location.href = pathArr[i].url;
    }
  }
});





//判断是否为IE
function isIE () {
  if (!!window.ActiveXObject || "ActiveXObject" in window) {
    return true;
  } else {
    return false;
  }
}


/**
 * @method gameTypeInit 生成下拉框选项
 * @param hasEl 生成有庒游戏的下拉框ID
 * @param notEl 生成无庄游戏的下拉框ID
 * */
function gameTypeInit (hasEl, notEl, empEl) {
  var gameTypeList = window.gameType;
  document.getElementById(hasEl).innerHTML = "";
  document.getElementById(notEl).innerHTML = "";
  for (var i = 0; i < gameTypeList.length; i++) {
    if (gameTypeList[i].banker === "HAS") {
      document.getElementById(hasEl).innerHTML += "<option value='" + gameTypeList[i].value + "'>" + gameTypeList[i].name + "</option>"
    }
    else if (gameTypeList[i].banker === "NOT") {
      document.getElementById(notEl).innerHTML += "<option value='" + gameTypeList[i].value + "'>" + gameTypeList[i].name + "</option>"
    }
    else {
      document.getElementById(empEl).innerHTML += "<option value='" + gameTypeList[i].value + "'>" + gameTypeList[i].name + "</option>"
    }
  }
}


/**
 * @method officialVerification 验证官方直播间上传是否违法
 * @param data 验证需要上传的数据
 * @return Object 返回验证状态以及验证消息
 * */
function officialVerification (data) {
  for (var key in data) {
    if (data[key] === "" || data[key] === undefined || data[key] === null || !data[key]) {
      var nullMsg = keyFilter(key);
      return {
        code: 400,
        msg: nullMsg + "为空",
        nullKey: key
      };
    }
  }
  if (data.roomDisplayId < 10001 || data.roomDisplayId > 10500) {
    return {
      code: 404,
      msg: "房间号ID范围从10001到10500"
    }
  }
  if (data.roomName.length > 8) {
    return {
      code: 405,
      msg: "房间名不得超过8个字符"
    }
  }
  if (data.liveUserName.length > 8) {
    return {
      code: 406,
      msg: "主播名不得超过8个字符"
    }
  }
  if (data.roomDisplayId < 10001 || data.roomDisplayId > 10500) {
    return {
      code: 407,
      msg: "房间号范围为 10001至10500"
    }
  }
  return {
    code: 200,
    msg: "验证通过"
  }
}

function keyFilter (key) {
  switch (key) {
    case "roomName":
      return "直播房间名";
      break;
    case "liveUserName":
      return "主播名字";
      break
    case "liveUserImg":
      return "主播头像";
      break
    case "roomImg":
      return "房间封面";
      break
    case "roomDisplayId":
      return "主播房间号"
  }
}

function upMsgVerification (msg) {
  if (msg === "" || msg === undefined || msg === null || !msg) {
    return false;
  }
  else {
    return true;
  }
}


/**
 * @method fun_date 计算n天后的日期
 * @param {int} num 可以为负数,从当天计算。
 * @return {string} n天后的日期，格式为YYYY-mm-dd
 * */
function fun_date (num) {
  var date1 = new Date();
  var time1 = date1.getFullYear() + "-" + (date1.getMonth() + 1) + "-" + date1.getDate()
  var date2 = new Date(date1);
  date2.setDate(date1.getDate() + num);
  var m;
  var d;
  if (date2.getMonth() + 1 < 10) m = "0" + (date2.getMonth() + 1);
  else m = date2.getMonth() + 1;
  if (date2.getDate() < 10) d = "0" + date2.getDate();
  else d = date2.getDate();
  var time2 = date2.getFullYear() + "-" + m + "-" + d;
  return time2;
}

//验证选择日期是否合法
function verificationTheTTime () {
  var open = new Date($("#theT_openTime").val());
  var end = new Date($("#theT_endTime").val());
  document.getElementById("theT_tips").innerText = "";
  if (open.getTime() > end.getTime()) {
    document.getElementById("theT_tips").innerText = "时间选择有误";
    return "err"
  }
  var day1 = formatSpecificDate(open);
  var day2 = formatSpecificDate(end);
  var d1 = day1.split("-");// [2018,8,16]
  var d2 = day2.split("-");// [2019,8,17]
  if ((d2[0] - d1[0]) > 1) {
    document.getElementById("theT_tips").innerText = "两个时间相差不得大于一年";
    return "err"
  } else if ((d2[0] - d1[0]) === 1 && d2[1] - d1[1] > 0) {
    document.getElementById("theT_tips").innerText = "两个时间相差不得大于一年";
    return "err"
  } else if ((d2[0] - d1[0]) === 1 && d2[1] - d1[1] >= 0 && d2[2] - d1[2] > 0) {
    document.getElementById("theT_tips").innerText = "两个时间相差不得大于一年";
    return "err"
  }
  return "ok"
}

function openDownloadDialog (url, saveName) {
  if (typeof url == 'object' && url instanceof Blob) {
    url = URL.createObjectURL(url); // 创建blob地址
  }
  var aLink = document.createElement('a');
  aLink.href = url;
  aLink.download = saveName || ''; // HTML5新增的属性，指定保存文件名，可以不要后缀，注意，file:///模式下不会生效
  var event;
  if (window.MouseEvent) event = new MouseEvent('click');
  else {
    event = document.createEvent('MouseEvents');
    event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
  }
  aLink.dispatchEvent(event);
}

function sheet2blob (sheet, sheetName) {
  sheetName = sheetName || 'sheet1';
  var workbook = {
    SheetNames: [sheetName],
    Sheets: {}
  };
  workbook.Sheets[sheetName] = sheet;
  // 生成excel的配置项
  var wopts = {
    bookType: 'xlsx', // 要生成的文件类型
    bookSST: false, // 是否生成Shared String Table，官方解释是，如果开启生成速度会下降，但在低版本IOS设备上有更好的兼容性
    type: 'binary'
  };
  var wbout = XLSX.write(workbook, wopts);
  var blob = new Blob([s2ab(wbout)], { type: "application/octet-stream" });
  // 字符串转ArrayBuffer
  function s2ab (s) {
    var buf = new ArrayBuffer(s.length);
    var view = new Uint8Array(buf);
    for (var i = 0; i != s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;
    return buf;
  }
  return blob;
}

/**
 * @method getdiffdate 计算两个日期内所有日期，包含这两个日期
 * @param {string} stime 开始时间  日期格式：YYYY-MM-DD
 * @param {string} etime 结束时间  日期格式：YYYY-MM-DD
 * @return {Array} 在stime和etime之中的所有日期的数组，包含stime和etime
 * */
function getdiffdate (stime, etime) {
  var diffdate = new Array();
  var i = 0;
  while (stime <= etime) {
    diffdate[i] = stime;
    var stime_ts = new Date(stime).getTime();
    var next_date = stime_ts + (24 * 60 * 60 * 1000);
    var next_dates_y = new Date(next_date).getFullYear() + '-';
    var next_dates_m = (new Date(next_date).getMonth() + 1 < 10) ? '0' + (new Date(next_date).getMonth() + 1) + '-' : (new Date(next_date).getMonth() + 1) + '-';
    var next_dates_d = (new Date(next_date).getDate() < 10) ? '0' + new Date(next_date).getDate() : new Date(next_date).getDate();
    stime = next_dates_y + next_dates_m + next_dates_d;
    i++;
  }
  return diffdate;
}

/**
 * @method timeDescendingOrder 根据时间降序排序包含时间对象的数组
 * @param {Array} arr 需要排序的包含时间对象的数组
 * @param {string} key 排序的时间字段
 * @return {Array} 根据指定时间字段降序排序的数组
* */
function timeDescendingOrder (arr, key) {
  arr.sort(function (a, b) {
    var t1 = new Date(Date.parse(a[key].replace(/-/g, "/")));
    var t2 = new Date(Date.parse(b[key].replace(/-/g, "/")));
    return t2.getTime() - t1.getTime()
  });
  return arr
}

/**
 * @method timeDescendingOrder 根据时间升序排序包含时间对象的数组
 * @param {Array} arr 需要排序的包含时间对象的数组
 * @param {string} key 排序的时间字段
 * @return {Array} 根据指定时间字段升序排序的数组
 * */
function timeAscendingOrder (arr, key) {
  arr.sort(function (a, b) {
    return b[key] < a[key] ? 1 : -1
  });
  return arr
}

/**
 * @method dateFormat 自定义格式时间格式化
 * @param {string} fmt 自定时间格式 如：YYYY-mm-dd HH:MM:SS
 * @param {string} t 时间对象
 * @return {string} 格式化后的时间
 * */
function dateFormat (fmt, t) {
  var date = new Date(t);
  var ret;
  var opt = {
    "Y+": date.getFullYear().toString(),
    "m+": (date.getMonth() + 1).toString(),
    "d+": date.getDate().toString(),
    "H+": date.getHours().toString(),
    "M+": date.getMinutes().toString(),
    "S+": date.getSeconds().toString()
  };
  for (var k in opt) {
    ret = new RegExp("(" + k + ")").exec(fmt);
    if (ret) {
      fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
    }
  }
  return fmt;
}

//去重
function unique (arr) {
  for (var i = 0; i < arr.length; i++) {
    for (var j = i + 1; j < arr.length; j++) {
      if (arr[i] == arr[j]) {         //第一个等同于第二个，splice方法删除第二个
        arr.splice(j, 1);
        j--;
      }
    }
  }
  return arr;
}


function dateMakeUp (dateArr) {
  var mArr = [
    { type: 31, value: "01" },
    { type: 31, value: "03" },
    { type: 30, value: "04" },
    { type: 31, value: "05" },
    { type: 30, value: "06" },
    { type: 31, value: "07" },
    { type: 31, value: "08" },
    { type: 30, value: "09" },
    { type: 31, value: "10" },
    { type: 30, value: "11" },
    { type: 31, value: "12" }
  ]
  for (var i = 0; i < dateArr.length - 1; i++) {
    var time1 = dateArr[i];
    var time2 = dateArr[i + 1];
    var leapYear = false;
    var time1Arr = time1.split("-");
    var time2Arr = time2.split("-");

    if (time1Arr[0] % 4 === 0 && time1Arr[0] % 100 !== 0) {
      leapYear = true
    }
    else if (time1Arr[0] % 400 === 0) {
      leapYear = true
    }
    if (leapYear) {
      mArr.push({ type: 29, value: "02" })
    } else {
      mArr.push({ type: 28, value: "02" })
    }
    //年
    if (time1Arr[0] === time2Arr[0]) {
      var nowMd;
      for (var x = 0; x < mArr.length; x++) {
        if (time1Arr[1] === mArr[x].value) {
          nowMd = mArr[x].type;
        }
      }
      //月
      if (time1Arr[1] === time2Arr[1]) {
        //日
        if (parseInt(time2Arr[2]) - parseInt(time1Arr[2]) === 1 && time1Arr[2] < nowMd && time2Arr[2] <= nowMd) {
          return "success";
        } else if (parseInt(time1Arr[2]) === parseInt(nowMd) && time2Arr[2] === "01") {
          return "success";
        } else {
          return dateArr[i];
        }

      } else if (parseInt(time2Arr[1]) - parseInt(time1Arr[1]) === 1 && parseInt(time1Arr[2]) === parseInt(nowMd) && time2Arr[2] === "01") {
        return "success";
      }
      else {
        return dateArr[i];
      }
    }
    else if (time1Arr[1] === "12" && time1Arr[2] === "31" && time2Arr[1] === "01" && time2Arr[2] === "01" && parseInt(time2Arr[0]) - parseInt(time1Arr[0]) === 1) {
      return "success";
    }
    else {
      console.log(1);
      return dateArr[i];
    }
  }
}

function getNowFormatDate (sdate, interval, caret) {
  var patt1 = /^\d{4}-([0-1]?[0-9])-([0-3]?[0-9])$/;
  if (!(sdate && typeof (sdate) == "string" && patt1.test(sdate))) {
    sdate = new Date();
  }
  interval = isNaN(parseInt(interval)) ? 0 : parseInt(interval);
  caret = (caret && typeof (caret) == "string") ? caret : "";
  var gdate = new Date(sdate).getTime();
  gdate = gdate + 1000 * 60 * 60 * 24 * interval;
  var speDate = new Date(gdate);
  var preYear = speDate.getFullYear();
  var preMonth = speDate.getMonth() + 1;
  var preDay = speDate.getDate();
  preMonth = (preMonth < 10) ? ("0" + preMonth) : preMonth;
  preDay = (preDay < 10) ? ("0" + preDay) : preDay;
  var preDate = preYear + caret + preMonth + caret + preDay;
  return preDate;
}


function userFilter (list) {
  for (var i = 0; i < list.length; i++) {
    if (list[i].phoneNumber === null || list[i].phoneNumber === "" || list[i].phoneNumber === undefined) {
      list[i].phoneNumber = "未绑定"
    }
    list[i].lastLoginTime = formatDate(list[i].lastLoginTime);
    switch (list[i].vipType) {
      case "LOCKED":
        list[i].vipType = "游客";
        break;
      case "RANGER":
        list[i].vipType = "游侠 VIP1";
        break;
      case "KNIGHT":
        list[i].vipType = "骑士 VIP2";
        break;
      case "BARON":
        list[i].vipType = "帝王 VIP3";
        break;
      case "ROYAL_FAMILY":
        list[i].vipType = "皇帝 VIP4";
        break;
      case "EMPEROR":
        list[i].vipType = "仙圣 VIP5";
        break;
      case "SAINT":
        list[i].vipType = "超神 VIP6";
        break;
    }
    list[i].platformTag = getFlatformForNum(list[i].platformTag)
    list[i].createDate = formatDate(list[i].createDate);
    if (list[i].online) {
      list[i].online = "在线"
    } else {
      list[i].online = "离线"
    }
    if (list[i].LiveUser) {
      list[i].LiveUser = "是"
    } else {
      list[i].LiveUser = "否"
    }
  }
  return list;
}

function randomNum (minNum, maxNum) {
  switch (arguments.length) {
    case 1:
      return parseInt(Math.random() * minNum + 1, 10);
    case 2:
      return parseInt(Math.random() * (maxNum - minNum + 1) + minNum, 10);
    default:
      return 0;
  }
}


function randomNumPlus (maxNum, minNum, decimalNum) {
  var max = 0, min = 0;
  minNum <= maxNum ? (min = minNum, max = maxNum) : (min = maxNum, max = minNum);
  switch (arguments.length) {
    case 1:
      return Math.floor(Math.random() * (max + 1));
      break;
    case 2:
      return Math.floor(Math.random() * (max - min + 1) + min);
      break;
    case 3:
      return (Math.random() * (max - min) + min).toFixed(decimalNum);
      break;
    default:
      return Math.random();
      break;
  }
}
var hasResetBtn
$("#resetBtn").on("click", function () {
  if (hasResetBtn) {

  } else {
    location.reload();
  }

});

function vipTypeFormat (vipType) {
  var tv;
  switch (vipType) {
    case "LOCKED":
      tv = "游客 VIP0";
      break;
    case "RANGER":
      tv = "游侠 VIP1";
      break;
    case "KNIGHT":
      tv = "骑士 VIP2";
      break;
    case "BARON":
      tv = "帝王 VIP3";
      break;
    case "ROYAL_FAMILY":
      tv = "皇帝 VIP4";
      break;
    case "EMPEROR":
      tv = "仙圣 VIP5";
      break;
    case "SAINT":
      tv = "超神 VIP6";
      break;
  }
  return tv
}

function gameTypeFormat (g) {
  for (var i = 0; i < window.gameType.length; i++) {
    if (g === window.gameType[i].type) {
      return window.gameType[i].name
    }
  }
}

function MillisecondToDate (msd) {
  var time = parseFloat(msd) / 1000; //先将毫秒转化成秒
  if (null != time && "" != time) {
    if (time > 60 && time < 60 * 60) {
      time = parseInt(time / 60.0) + "分" + parseInt((parseFloat(time / 60.0) - parseInt(time / 60.0)) * 60) + "秒";
    } else if (time >= 60 * 60 && time < 60 * 60 * 24) {
      time = parseInt(time / 3600.0) + "时" + parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60) + "分" + parseInt((parseFloat((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60) - parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60)) * 60) + "秒";
    } else {
      time = parseInt(time) + "秒";
    }
  }
  return time;
};


/**
 * 格式化秒
 * @param int  value 总秒数
 * @return string result 格式化后的字符串
 */
function formatSeconds (value) {
  if (value === 0 || value === undefined || value === "" || value === "0") {
    return "00:00:00"
  }
  var theTime = parseInt(value / 1000);// 需要转换的时间秒
  var theTime1 = 0;// 分
  var theTime2 = 0;// 小时
  var theTime3 = 0;// 天
  if (theTime > 60) {
    theTime1 = parseInt(theTime / 60);
    theTime = parseInt(theTime % 60);
    if (theTime1 > 60) {
      theTime2 = parseInt(theTime1 / 60);
      theTime1 = parseInt(theTime1 % 60);
    }
  }
  var result = '';
  theTime = theTime < 10 ? "0" + theTime : theTime + ""
  theTime1 = theTime1 < 10 ? "0" + theTime1 : theTime1 + ""
  theTime2 = theTime2 < 10 ? "0" + theTime2 : theTime2 + ""
  result = "" + theTime;
  result = "" + theTime1 + ":" + result;
  result = "" + theTime2 + ":" + result;
  return result;
}


function GetQueryString (name) {
  var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
  var r = window.location.search.substr(1).match(reg);  //获取url中"?"符后的字符串并正则匹配
  var context = "";
  if (r != null)
    context = r[2];
  reg = null;
  r = null;
  return context == null || context == "" || context == "undefined" ? "" : context;
}

function betTypeFormat (g) {
  for (var i = 0; i < window.betType.length; i++) {
    if (g === window.betType[i].type) {
      return window.betType[i].name
    }
  }
}

function getGiftMsg (g) {
  for (var i = 0; i < window.gift.length; i++) {
    if (g === window.gift[i].id) {
      return window.gift[i]
    }
  }
}

function getPlatform (g) {
  if (g === "PLATFORM_JINLI") {
    return "锦鲤平台"
  } else if (g ==="PLATFORM_T") {
    return "T平台"
  } else if (g ==="PLATFORM_Q") {
    return "Q平台"
  }
  return ""
}

function sharedPlatformFilters(list) {
    let text = ""
    if(list.length === 3){
      return "全部"
    }
    for(let i=0;i<list.length;i++){
        text+=getPlatform(list[i]) +"、"
    }
    return  text.slice(0,text.length-1)
}

function getFlatformForNum (g) {
  if (g === 1) {
    return "锦鲤平台"
  } else if (g === 2) {
    return "T平台"
  } else if (g === 3) {
    return "Q平台"
  }
}

function getPlatFormId(g) {
    switch (g) {
        case "锦鲤平台":
          return 1
        case "T平台":
          return 2
        case "Q平台":
          return 3
    }
}

function brandFormat (b, m) {
  if (b !== "BRAND_OTHER") {
    for (var i = 0; i < window.brand.length; i++) {
      if (window.brand[i].type === b) {
        return window.brand[i].name + m
      }
    }
  } else {
    return "其他型号"
  }
}

function brandForNum (b) {
  if (b !== "0") {
    for (var i = 0; i < window.brand.length; i++) {
      if (window.brand[i].num === b) {
        return window.brand[i].name
      }
    }
  } else {
    return "其他型号"
  }
}

function raceFormat (res) {
  switch (res) {
    case "LANDLORDS":
      return "斗地主"
    case "TEXAS":
      return "德州扑克"
    case "GOLDEN_FLOWER":
      return "炸金花"
  }
}

function objectEmpty (obj) {
  for (let key in obj) {
    return false;    //非空
  }
  return true;       //为空
}


function roleFormat (data) {
  switch (data) {
    case "OPERATIONUSERCOIN_b":
      return "OPERATIONUSERCOIN"
    case "BECOMELIVEUSER_b":
      return "BECOMELIVEUSER"
    case "CHANGEPASSWORD_b":
      return "CHANGEPASSWORD"
    default:
      return data
  }
}
function roleArrFormat (list) {
  for (let i = 0; i < list.length; i++) {
    list[i] = roleFormat(list[i])
  }
  return unique(list)
}

function urlEncode (param, key, encode) {
  if (param == null) return '';
  var paramStr = '';
  var t = typeof (param);
  if (t == 'string' || t == 'number' || t == 'boolean') {
    paramStr += '&' + key + '=' + ((encode == null || encode) ? encodeURIComponent(param) : param);
  } else {
    for (var i in param) {
      var k = key == null ? i : key + (param instanceof Array ? '[' + i + ']' : '.' + i);
      paramStr += urlEncode(param[i], k, encode);
    }
  }
  return paramStr;
};


function parseURL (url) {
  if (!url) return;
  url = decodeURI(url);
  var url = url.split("?")[1];
  var para = url.split("&");
  var len = para.length;
  var res = {};
  var arr = [];
  for (var i = 0; i < len; i++) {
    arr = para[i].split("=");
    res[arr[0]] = arr[1];
  }
  return res;
}


function displayRoleClassElement (needRole, element) {
  var roleList = JSON.parse(localStorage.getItem("loginMsg")).role
  for (let i = 0; i < roleList.length; i++) {
    if (roleList[i] === "ADMIN") {
      return "success"
    }
    for (let x = 0; x < needRole.length; x++) {
      if (roleList[i] === needRole[x]) {
        return "success"
      }
    }
  }
  $("." + element).attr("style", "display:none")
  return "err"
}


function displayRoleElement (needRole, elementId) {
  var roleList = JSON.parse(localStorage.getItem("loginMsg")).role
  for (let i = 0; i < roleList.length; i++) {
    if (roleList[i] === "ADMIN") {
      return "success"
    }
    for (let x = 0; x < needRole.length; x++) {
      if (roleList[i] === needRole[x]) {
        return "success"
      }
    }
  }
  $("#" + elementId).attr("style", "display:none")
  return "err"
}

function roleTableBtn (needRole, btnName) {
  var roleList = JSON.parse(localStorage.getItem("loginMsg")).role
  for (let i = 0; i < roleList.length; i++) {
    if (roleList[i] === "ADMIN") {
      return
    }
    for (let x = 0; x < needRole.length; x++) {
      if (roleList[i] === needRole[x]) {
        return
      }
    }
  }
  $("a[lay-event='" + btnName + "']").attr("class", "layui-btn layui-btn-sm layui-btn-radius layui-btn-disabled");
  $("a[lay-event='" + btnName + "']").attr("lay-event", "null")
}

function getCheckedData (list) {
  var roleList = []
  for (var i = 0; i < list.length; i++) {
    roleList.push(list[i].id)
    if (list[i].children.length > 0) {
      roleList.push(...getCheckedData(list[i].children))
    }
  }
  return roleList
}


function initPlatformCheakBox(list,platform) {
    // <input type="radio" name="sharedPlatform" value="MUTE_REACTIONARY" title="锦鲤直播" >
    //     <input type="radio" name="sharedPlatform" value="PLATFORM_T" title="T平台">
    //     <input type="radio" name="sharedPlatform" value="PLATFORM_Q" title="Q平台">
    var radio = [
        {id:1,val:'<input type="radio" name="sharedPlatform" value="PLATFORM_JINLI" title="锦鲤直播" checked="checked" >'},
        {id:2,val:''},
        {id:3},
    ]

    switch (platform) {
        case 1:

    }
}

function getRadio(list,platform) {
    var hasJinLi = ""
    var hasT = ""
    var hasQ = ""
    for(let i =0;i<list.length;i++){
      if(list[i] === "PLATFORM_JINLI"){
          hasJinLi = "checked"
      }
        if(list[i] === "PLATFORM_T"){
          hasT = "checked"
        }
        if(list[i] === "PLATFORM_Q"){
          hasQ = "checked"
        }
    }
    switch (platform) {
        case 1:

          return `  <form class="layui-form checkBox">
            <input type="checkbox" lay-filter="sharedPlatform" name="sharedPlatform" value="PLATFORM_T" title="T平台" ${hasT}> 
                    <input type="checkbox" lay-filter="sharedPlatform"  name="sharedPlatform" value="PLATFORM_Q" title="Q平台"  ${hasQ}></form>`
        case 2:
          return `<form class="layui-form checkBox"><input type="checkbox" lay-filter="sharedPlatform"  name="sharedPlatform" value="PLATFORM_JINLI" title="锦鲤直播"  ${hasJinLi}>
                    <input type="checkbox" lay-filter="sharedPlatform"  name="sharedPlatform" value="PLATFORM_Q" title="Q平台" ${hasQ}></form>`
        case 3:
          return `<form class="layui-form checkBox"><input type="checkbox" lay-filter="sharedPlatform"   name="sharedPlatform" value="PLATFORM_JINLI" title="锦鲤直播" ${hasJinLi}>
                    <input type="checkbox"  lay-filter="sharedPlatform" name="sharedPlatform" value="PLATFORM_T" title="T平台" ${hasT}></form>`
    }


}

function getWatchTimeforUserList() {
    var sh,sm,ss,eh,em,es,startOfLiveWatchTime,endOfLiveWatchTime
    sh = parseInt($("#staH").val()===""?0:$("#staH").val())
    sm = parseInt($("#staM").val()===""?0:$("#staM").val())
    ss = parseInt($("#staS").val()===""?0:$("#staS").val())
    eh = parseInt($("#etaH").val()===""?0:$("#etaH").val())
    em = parseInt($("#etaM").val()===""?0:$("#etaM").val())
    es = parseInt($("#etaS").val()===""?0:$("#etaS").val())
    startOfLiveWatchTime = (sh*60*60*1000)+(sm*60*1000)+(ss*1000)
    endOfLiveWatchTime = (eh*60*60*1000)+(em*60*1000)+(es*1000)
    if(endOfLiveWatchTime>0){
        endOfLiveWatchTime = (eh*60*60*1000)+(em*60*1000)+(es*1000)+999
    }
    return {startOfLiveWatchTime,endOfLiveWatchTime}
}

$(function(){
    $(document).ajaxError(
        function(event,xhr,options,exc ){
            if(xhr.status === 'undefined'){
                return;
            }
            switch(xhr.status){
                case 403:
                    alert("系统拒绝：您没有访问权限。");
                    break;
                case 404:
                    alert("您访问的资源不存在。");
                    break;
                case 500:
                    console.log(xhr);
                    alert("请求发生错误")
            }
        }
    );
});


function tableFilter(list) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].avatarUrl === null) {
            list[i].head = window.config.imgUrl + "/images/avatar/default/img_avatar.png"
        } else {
            list[i].head = window.config.imgUrl + list[i].avatarUrl
        }
        list[i].loginTime = dateFormat("YYYY-mm-dd HH:MM:SS", list[i].loginTime)
        list[i].createTime = dateFormat("YYYY-mm-dd HH:MM:SS", list[i].createTime)

        list[i].avgWatchLiveTime = formatSeconds(list[i].avgWatchLiveTime)
        list[i].watchLiveTime = formatSeconds(list[i].watchLiveTime)
        list[i].platformTag = getFlatformForNum(list[i].platformTag)
        list[i].status =  list[i].statue === "ACCOUNT_BAN"?"已封禁": "正常"
    }
    return list
}

function livePatternFilter(res) {
  var r = ""
    switch (res) {
        case "LIVE_AUDIO":
            r = "音频"
            break
        case "LIVE_VIDEO":
            r = "视频"
            break
    }
    return r
}