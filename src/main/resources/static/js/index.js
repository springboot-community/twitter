
;(function(){
	/**
	 * 初始化Websocket
	 */
	const {protocol, host} = window.location;
	const websocket = new WebSocket(`${protocol === 'https:' ? 'wss:': 'ws:'}//${host}/channel/twitter/${USER_ID}?token=${TOKEN}`);
	websocket.onmessage = e => {
		const message = JSON.parse(e.data);
		console.log('收到消息:', message);
	}
	websocket.onclose = e => {
		let {code, reason} = e;
		if (!reason){
			if (code === 1006){
				reason = "服务器停止运行";
			} else if (code === 1003){
				reason = "不接受的数据类型";
			} else if (code === 1008){
				reason = "不接收的数据格式";
			} else if (code === 1009){
				reason = "数据过大";
			} else {
				reason = "未知原因";
			}
		}
		console.log(`链接断开:code=${code}, reason=${reason}`);
		// TODO 弹窗提示，“与服务器断开链接：${reason}”
	}
	websocket.onopen = () => {
		console.log(`链接建立...`);
	}
	websocket.onerror = e => {
		console.log('链接异常:', e);
	}
})();
