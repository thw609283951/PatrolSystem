var a = 5;
var b = 2;
//今日巡河

function patrolRiver_today(count,todayCount){
	var option3 = {
		title: {
			text: '',
			subtext: '',
			x: ''
		},
		tooltip: {
			show:false,
			trigger: 'item',
			formatter: "{a} <br/>{b} : {c} ({d}%)"
		},
		//  color:['#ebe9e9','#87CEFA'] ,
		legend: {
			show: false,
			orient: 'horizontal',
			x: 'center',
			data: ['今日', '总数']
		},

		calculable: true,
		series: [{
				name: '巡查工地数',
				type: 'pie',
				radius: '55%',
				center: ['50%', '45%'],
				data: [{
						value: count-todayCount,
						name: '总数',
						itemStyle: {
							color: "#D3D3D3"
						},
						label: {
							color: "black",
							position: 'inner',
							show: true,
							formatter: '总工地数 :' + count + '\n已巡查数:' + todayCount
						},
						labelLine: {
							show: true
						}

					},
					{
						 
						
						value: todayCount,
						name: '今日',
						itemStyle: {
							color: "#87CEFA"
						},
						label: {
							position: 'inner',
							show: true,
							formatter: '{b} : {d}%'
						},
						labelLine: {
							show: true
						}
					}
				]

			}

		]
	};
	var myChart3 = echarts.init(document.getElementById('mian3'));
	myChart3.setOption(option3);
	
	
}

	
	
	function patrolRiver_yesterday(count,yesCount){
		//昨日巡河
	var option3_ = {
		title: {
			text: '',
			subtext: '',
			x: ''
		},
		tooltip: {
			show:false,
			trigger: 'item',
			formatter: "{a} <br/>{b} : {c} ({d}%)"
		},
		//color:['#ebe9e9',#458B00'] ,
		legend: {
			show: false,
			orient: 'horizontal',
			x: 'center',
			data: ['昨日', '总数']
		},

		calculable: true,
		series: [{
				name: '巡查工地数',
				type: 'pie',
				radius: '55%',
				center: ['50%', '45%'],
				data: [{
						value: count-yesCount,
						name: '总数',
						itemStyle: {
							color: "#D3D3D3"
						},
						label: {
							color: "black",
							position: 'inner',
							show: true,
							formatter: '总工地数 :' + count + '\n已巡查数:' + yesCount
						},
						labelLine: {
							show: true
						}

					},
					{
						value: yesCount,
						name: '昨日',
						itemStyle: {
							color: "#458B00"
						},
						label: {
							position: 'inner',
							show: true,
							formatter: '{b} : {d}%'
						},
						labelLine: {
							show: true
						}
					}
				]

			}

		]
	};
	var myChart3_ = echarts.init(document.getElementById('mian3_'));
	myChart3_.setOption(option3_);
	
	 
	}
	


//全市巡查工地统计
function patrolRiver_all(valueArr,todayArr,yesArr){
	
	document.getElementById('mian4').style.height=valueArr.length*120+'px';
	
	//全市巡河统计
	var option4 = {
		title: {

		},
		color: ['#87CEFA', '#458B00'],
		tooltip: {
			trigger: 'axis',
			
			axisPointer: {
				type: 'shadow'
			}
		},
		legend: {
			
			data: ['今日巡查', '昨日巡查']
		},
		grid: {
			left: '3%',
			right: '4%',
			bottom: '3%',
			containLabel: true
		},
		xAxis: {
			type: 'value',
			boundaryGap: [0, 0.01],
			min:0,
			max:100
			
		},
		yAxis: {
			type: 'category',
			data: valueArr
		},
		series: [{
				name: '今日巡查',
				type: 'bar',
				data: todayArr
			},
			{
				name: '昨日巡查',
				type: 'bar',
				data: yesArr
			}
		]
	};
	var myChart4 = echarts.init(document.getElementById('mian4'));
	myChart4.setOption(option4);
	myChart4.on('click',function(para){
		//console.log(para);
		golbalWsId=para.data.id;
		getUserByPara(para.data.id,1);
	});
}



