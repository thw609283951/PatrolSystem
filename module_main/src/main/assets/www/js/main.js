var HTTPURL = "http://gxt.supersit.com:8061/xct/";
var golbalWsId = "";
//获取总数   及所有工地
function getCountPatrol() {
	var xmlHttp = null;
    var userId = control.getUserId();
	if(window.XMLHttpRequest) {
		xmlHttp = new XMLHttpRequest();
	} else if(window.ActiveXObject) { // code for IE5 and IE6
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}

	if(xmlHttp != null) {
		xmlHttp.onreadystatechange = function() {
			if(xmlHttp.readyState == 4) {
				if(xmlHttp.status == 200) {
					var result = JSON.parse(xmlHttp.responseText);
					//console.log(result);
					document.getElementById('count').innerText = result.data.deptCount;
					document.getElementById('tdCount').innerText = result.data.todayCount;
					document.getElementById('yesCount').innerText = result.data.yesterdayCount;
					patrolRiver_today(result.data.deptCount, result.data.todayCount);
					patrolRiver_yesterday(result.data.deptCount, result.data.yesterdayCount);
					var valueArr = [];
					var todayArr = [];
					var yesArr = [];
					
					//patrolRiver_all(valueArr, todayArr, yesArr);
					deptList(result.data.depts);
				} else {
					console.log('errorssssssssssssss');
				}

			}

		}
		xmlHttp.open('GET', HTTPURL + "app/workpast/count/"+userId, true);
		xmlHttp.send(null);
	}
}
getCountPatrol();

//获取所有工地
//通过工地id获取人员

function getUserByPara(wsId, mask) {
	var xmlHttp = null;

	if(window.XMLHttpRequest) {
		xmlHttp = new XMLHttpRequest();
	} else if(window.ActiveXObject) { // code for IE5 and IE6
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}

	if(xmlHttp != null) {
		xmlHttp.onreadystatechange = function() {
			if(xmlHttp.readyState == 4) {
				if(xmlHttp.status == 200) {

					var result = JSON.parse(xmlHttp.responseText);
					var html = '';
					for(var i = 0; i < result.data.users.length; i++) {
						html += '<div class="p_person_list">';
						html += '<div class="p_person_top">';
						html += '<div class="p_person_job">';
						if(result.data.users[i].userHeadPath.indexOf("-1")){

						    html += '<img src="img/ic_user_avatar.png" /> ' + result.data.users[i].userPosition + '：' + result.data.users[i].userName + '</div>';

						}else{
						    html += '<img src="' + result.data.users[i].userHeadPath + '" /> ' + result.data.users[i].userPosition + '：' + result.data.users[i].userName + '</div>';
						}

						if(mask == 1) { //jintian
							if(result.data.users[i].todayWorkpasts.length == 0) {
								html += '<div class="no_patrol">未巡查</div>';
							} else {
								html += '<div class="is_patrol">已巡查</div>';
							}
						} else { //zuotian 
							if(result.data.users[i].yesterdayWorkpasts.length == 0) {
								html += '<div class="no_patrol">未巡查</div>';
							} else {
								html += '<div class="is_patrol">已巡查</div>';
							}
						}
						html += '<div class="cl"></div>';
						html += '</div>';
						html += '<div class="p_person_post p_person_bt">';
						html += '<div class="p_person_field">职务</div>';
						html += '<div class="p_fr">' + result.data.users[i].userPosition + '</div>';
						html += '<div class="cl"></div>';
						html += '</div>';
						html += '<div class="p_person_post">';
						html += '<div class="p_person_field">电话</div>';
						html += '<div class="p_fr phone"><img src="img/phone.png" />' + result.data.users[i].userMobileTel + '</div>';
						html += '<div class="cl"></div>';
						html += '</div>';
						html += '</div>';
					}

					document.getElementById('wsList').innerHTML = html;
					document.getElementById('warp').style.display = "none";
					document.getElementById('wsUser').style.display = "block";

				} else {
					console.log('errorssssssssssssss');
				}

			}

		}
		xmlHttp.open('GET', HTTPURL + "app/dept/" + wsId, true);
		xmlHttp.send(null);
	}
}
//今日巡查
document.getElementById('ts_').addEventListener('click', function() {
	document.getElementById('ts_').className = "p_river_tod p_river_active";
	document.getElementById('ys_').className = "p_river_tod";
	getUserByPara(golbalWsId, 1);
});
//昨日巡查
document.getElementById('ys_').addEventListener('click', function() {
	document.getElementById('ys_').className = "p_river_tod p_river_active";
	document.getElementById('ts_').className = "p_river_tod";
	getUserByPara(golbalWsId, 2);
});
//back
document.getElementsByClassName('backContainer')[0].addEventListener('click', function() {
	document.getElementById('wsUser').style.display = "none";
	document.getElementById('warp').style.display = "block";
});

function deptList(result) {
	var html = '';

	for(var i = 0; i < result.length; i++) {
		html += '<div class="status_list" mask="'+result[i].deptId+'" >';
		html += '<div class="p_person_top gdname">'+result[i].deptName+'</div>';
		html += '<div>';
		html += '<div class="status">';
		html += '<div class="fl t-type">今日状态</div>';
		if(result[i].todayDeptStatus){
			html += '<div class="fl typeFull is"></div>';
		}else{
			html += '<div class="fl typeFull no"></div>';
		}
		
		
		html += '<div class="cl"></div>';
		html += '</div>';
		html += '<div class="st-b">';
		html += '<div class="fl t-type">昨日状态</div>';
		if(result[i].yesterdayDeptStatus){
			html += '<div class="fl typeFull is"></div>';
		}else{
			html += '<div class="fl typeFull no"></div>';
		}
		
		html += '</div>';
		html += '<div class="cl"></div>';
		html += '<div class="p_zy"></div>';
		html += '</div>';
		html += '</div>';

	}

	document.getElementById('works').innerHTML = html;
	
	
	
	var list=document.getElementsByClassName('status_list');
	
	for (var i=0;i<list.length;i++) {
		list[i].addEventListener('click',function(){
			golbalWsId=this.getAttribute('mask');
		    getUserByPara(this.getAttribute('mask'), 1);
	});
	}
	
	
	
}