var imgUrl = [];

function intiUpLoad (elm, callback) {
  console.log(elm);
  var el = document.getElementById(elm);
  console.log(el);
  el.innerHTML += '<label type="button" class="xdg_LoadImgBtn" id="' + elm + 'Btn">' +
    '<span>选择图片</span>' +
    '<input type="file">' +
    '</label>'
  document.getElementById(elm).addEventListener('change', function (e) {
    var files = e.target.files;
    console.log(files);
    if (!files.length) return;
    var formData = new FormData();
    formData.append("handlerType", 4);
    formData.append('file', files[0]);

    $.post({
      url: window.config.imgUrl + "/uploadImage",
      data: formData,
      processData: false,
      contentType: false,
      dataType: "json",
      cache: false,
      success: function (res) {
        console.log(res);
        callback(res[0])
      },
      error: function () {
        layer.open({
          title: '错误信息'
          , content: '图片上传出错'
        });
      }
    })
  });
}