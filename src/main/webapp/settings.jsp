<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="header">
        <h1>My settings</h1>
    </jsp:attribute>
	<jsp:body>


        <!-- timepicker -->
        
<!-- 		<form action="confs" method="POST" role="form" autocomplete="off" id="confform" > -->
        <form class="form-horizontal" method="POST" >
            <div class="form-group">
                <label class="col-sm-2 control-label" for="inputEmail">Email address</label>
                <div class="col-sm-10">
                    <div class="row">
                        <div class="col-sm-6">
                            <label class="radio-inline">
                                <input type="radio"
                                name="emailRadio"
								id="emailRadio"
								value="cern"
								<c:if test="${!user.useCustomEmail}">checked</c:if>
								checked
							> Use e-mail from CERN database
                            </label>
                            <input type="email" class="form-control"
								id="inputCernEmail" placeholder="Email"
								value="<c:out
								value="${user.cernEmail}" />" disabled
							>
                        </div>
                        <div class="col-sm-6">

                            <label class="radio-inline">
                                <input type="radio"
                                name="emailRadio"
								id="emailRadio"
								value="other"
								<c:if test="${user.useCustomEmail}">checked</c:if>
							> Use other e-email
                            </label>
                            <input type="email" class="form-control"
								id="inputEmail" name="inputEmail" placeholder="Email" value="<c:out
								value="${user.email}" />"
							>
                            </div>
                            <div class="col-sm-12">
                                <p class="help-block">Notifications with e-mail channel will be sent to selected address</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label"
					for="inputPhone"
				>Phone number</label>
                <div class="col-sm-10">
                    <div class="row">
                        <div class="col-sm-6">
                            <label class="radio-inline">
                                <input type="radio" 
                                name="phoneRadio"
								id="phoneRadio"
								value="cern"
								<c:if test="${!user.useCustomPhone}">checked</c:if>
							> Use number from CERN database
                            </label>
                            <input type="text" class="form-control"
								id="inputCernPhone" placeholder="No phone number" 
								value="<c:out
								value="${user.cernPhone}" />" disabled
							>
                        </div>
                        <div class="col-sm-6">

                            <label class="radio-inline">
                                <input type="radio"
                                name="phoneRadio"
								id="phoneRadio"
								value="other"
								<c:if test="${user.useCustomPhone}">checked</c:if>
							> Use other number
                            </label>
                            <input type="text" class="form-control"
								id="inputPhone" placeholder="Phone number"
								name="inputPhone" 
								value="<c:out
								value="${user.phone}" />"
							>
                        </div>
                            <div class="col-sm-12">
                                <p class="help-block">Notifications with SMS channel will be sent to selected number</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label"
					for="inputDaytime"
				>My daytime</label>
                    <div class="col-sm-10">
                        <div class="row">
                            <div class="col-sm-6">

                                <div class="input-group clockpicker">
                                    <span class="input-group-addon">starts at</span>
                                    <input type="text"
									class="form-control" value="08:30"
								>
                                    <span class="input-group-addon">
<span class="fa fa-sun-o"></span>
                                    </span>
                                </div>
                            </div>
                            <div class="col-sm-6">
                                <div class="input-group clockpicker">
                                    <span class="input-group-addon">ends at</span>
                                    <input type="text"
									class="form-control" value="21:30"
								>
                                    <span class="input-group-addon">
<span class="fa fa-moon-o"></span>
                                    </span>
                                </div>
                            </div>
                        </div>
                        <p class="help-block">This setting will be used for time based conditions: <abbr
							title="Time based condition to get notification only at daytime"
						>daytime</abbr> and <abbr
							title="Time based condition to get notification only at night-time"
						>night-time</abbr>
                        </p>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label"
					for="inputBefore"
				>Shift correction</label>
                    <div class="col-sm-10">
                        <div class="row">

                            <div class="col-sm-6">
                                <input id="demo1" type="text" value="15"
								name="demo1"
							>
                            </div>
                            <div class="col-sm-6">
                                <input id="demo1" type="text" value="0"
								name="demo2"
							>
                            </div>
                        </div>
                        <p class="help-block">This setting will be used for shift based condition: <abbr
							title="Shift based condition to get notification only during my shift"
						>during my shift</abbr> 
                        </p>
                    </div>

                </div>

                <button type="submit" class="btn btn-success">Save changes</button>
            </form>




            <script type="text/javascript"
			src="js/jquery.bootstrap-touchspin.min.js"
		></script>
            <script>
													$("input[name='demo1']")
															.TouchSpin(
																	{
																		min : -240,
																		max : 240,
																		step : 15,
																		decimals : 0,
																		postfix : 'adjust shift start'
																	});

													$("input[name='demo2']")
															.TouchSpin(
																	{
																		min : -240,
																		max : 240,
																		step : 15,
																		decimals : 0,
																		postfix : 'adjust shift end'
																	});
												</script>


        <script type="text/javascript"
			src="js/bootstrap-clockpicker.min.js"
		></script>
        <script type="text/javascript">
									$('.clockpicker').clockpicker({
										autoclose : true,
										donetext : ""
									});
								</script>

    </jsp:body>
</t:genericpage>