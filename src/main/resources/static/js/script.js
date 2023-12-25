//sidebar 
const toggleSidebar = () => {
	if ($(".side-bar").is(":visible")) {
		$(".side-bar").css("display", "none");
		$(".content").css("margin-left", "0%");

	}
	else {
		$(".side-bar").css("display", "block");
		$(".content").css("margin-left", "20%");

	}
};

//search box
const search = () => {

	//console.log("searching...")

	//let query = $('[name="query"]');

	let query = $("#search-input").val();


	if (query == "") {
		$(".search-result").hide();
	}
	else {
		console.log(query);

		//sending request to server
		var url = 'http://localhost:8080/search/${query}';
		fetch(url).then((response) => {
			return response.json();
		})
			.then((data) => {
				console.log(data);

				let text = '<div class="list-group">';

				data.forEach((contact) => {
					text += '<a href="/user/${contact.cid}/Contact" class="list-group-item list-group-item-action"> ${contact.name} </a>';
				});
				text += '</div>';

				$(".search-result").html(text);
				$(".search-result").show();
			});

	}
}


//payment integration
//first request to server to create order
const payment = () => {
	console.log("payment starting");
	let amount = $('#payment_field').val();
	console.log(amount);
	if (amount == '' || amount == null) {
		//alert("Amount is required!!");
		swal("Failed!", "Amount is required!", "error");
		return;
	}
	//code
	//using ajax to send request to server and create order
	$.ajax(
		{
			url: '/user/create_order',
			data: JSON.stringify({ amount: amount, info: 'order_request' }),
			contentType: 'application/json',
			type: 'POST',
			dataType: 'json',
			success: function(response) {
				//invoke when sucess
				console.log(response);
				if (response.status == 'created') {
					//open payment form
					let options = {
						key: "rzp_test_CJghtqd2CwnqLe",
						amount: response.amount,
						currency: "INR",
						name: "Smart Contact Manager",
						description: "Donation",
						image: 'https://cdn.iconscout.com/icon/free/png-256/free-smart-manager-461753.png',
						order_id: response.id,
						handler: function(response) {
							console.log(response.razorpay_payment_id)
							console.log(response.razorpay_order_id)
							console.log(response.razorpay_signature)
							console.log("Payment Successful!!");

							updatePaymentOnServer(response.razorpay_payment_id, response.razorpay_order_id, 'paid');



						},
						"prefill": {
							"name": " ",
							"email": " ",
							"contact": " "
						},
						"notes": {
							"address": "Smart Contact Corporate Office"

						},
						"theme": {
							"color": "#3399cc"
						}

					};

					let rzp = new Razorpay(options)
					rzp.on('payment.failed', function(response) {
						console.log(response.error.code);
						console.log(response.error.description);
						console.log(response.error.source);
						console.log(response.error.step);
						console.log(response.error.reason);
						console.log(response.error.metadata.order_id);
						console.log(response.error.metadata.payment_id);
						swal("Oops!", "Payment Failed!", "error");
					});
					rzp.open()
				}

			},
			error: function(error) {
				//invoke when error
				//console.log(error);
				alert("Something went wrong!!");
			}
		}
	);
};


function updatePaymentOnServer(payment_id, order_id, status) {
	$.ajax(
		{
			url: '/user/update_order',
			data: JSON.stringify({ payment_id: payment_id, order_id: order_id, status: status }),
			contentType: 'application/json',
			type: 'POST',
			dataType: 'json',
			success: function(response) {
				swal("Congrats!", "Payment Successful!", "success");

			},
			error: function(error) {
				swal("Failed!", "Your payment is successful! but we did not get on server.. we will contact you as soon as possible  ", "error");
			}
		}

	)
}




