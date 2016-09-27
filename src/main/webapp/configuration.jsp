<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="header">
      <h1>Configurations</h1>
    </jsp:attribute>
	<jsp:body>
	
      	<div class="control-group" id="fields">
        	<div class="controls">
		<form action="confs" method="POST" role="form" autocomplete="off" id="confform" >
			<table class="table form-table">
				<tr>
					<th>Event</th>
					<th>Importance</th>
					<th>Shift Preferences</th>
					<th>Time Preferences</th>
					<th>Channel</th>
					<th>Action</th>
				</tr>
				
						<tr class="entry hidden" >
						<!-- event column -->
						<td>
						<input name="entry_blank" value="blank" class="hidden hidden_input_id" ></input>
						<select
						    	class="multiselect_blank" 
						    	multiple="multiple"
						    	name="event_conf_blank" >
						    	<c:forEach items="${eventTypeList}" var="event">
									<option 
										 
										value="<c:out value="${event.id}" />">
											<c:out value="${event.name}" />
									</option>
						        
								</c:forEach>
						   
						    </select>
						    
						</td>
						<!-- importance column -->
						<td>
						    <select 
						    	class="multiselect_blank" 
						    	multiple="multiple"
						    	name="importance_conf_blank">
						    	
						    	<c:forEach items="${conditionList}" var="timeCondition">
									<option 
										value="<c:out value="${timeCondition.ordinal}" />">
											<c:out value="${timeCondition.name}" />
									</option>
						        
								</c:forEach>
						   
						    </select>
						    
						
						</td>
						
						<!-- shift preferences column -->
						<td>
						
						    <select 
						    	class="multiselect_blank"
						    	multiple="multiple"
						    	name="shift_conf_blank">
						    	
						    	<c:forEach items="${shiftConditionList}" var="timeCondition">
									<option 
										
										value="<c:out value="${timeCondition.ordinal}" />">
											<c:out value="${timeCondition.name}" />
									</option>
						        
								</c:forEach>

						    </select>
							

						</td>
						
						<!-- time preferences column -->
						<td>
						
							<select 
						    	
						    	class="multiselect_blank"
						    	multiple="multiple"
						    	name="time_conf_blank">
						    	
						    	<c:forEach items="${timeConditionList}" var="timeCondition">
									<option 
										
										value="<c:out value="${timeCondition.ordinal}" />">
											<c:out value="${timeCondition.name}" />
									</option>
						        
								</c:forEach>

						    </select>
							

						</td>
						
						<!-- channel column -->
						<td>
						
						
						<select 
							name="channel_conf_blank" 
						    	class="multiselect_blank" 
							multiple="multiple">
							
							<c:forEach items="${channelList}" var="channel">
								<option 
									
									value="<c:out value="${channel.ordinal}" />">
										<c:out value="${channel.name}" />
								</option>
						        
							</c:forEach>
							
						    </select>

						</td>
						<td>
	              		<button class="btn btn-danger btn-remove" type="button">
	                                <span class="fa fa-remove"></span>
	                	</button>
	                	
	                	
						</td>
					</tr>   
    
				<c:forEach items="${configurations}" var="configuration">
				
						
				
					<tr class="entry ">
						<!-- event column -->
						<td>
						<input name="entry[]" value="<c:out value="${configuration.id }"/>" class="hidden" ></input>
						<div class="has_error">
						<select 
						    	class="multiselect" 
						    	multiple="multiple"
						    	name="event_conf_<c:out value="${configuration.id}" />">
						    	
						    	<c:forEach items="${eventTypeList}" var="event">
									
									<option 
										<c:if test="${eventMode[configuration.id][event.id] }">selected="selected"</c:if> 
										value="<c:out value="${event.id}" />">
											<c:out value="${event.name}" />
									</option>
						        
								</c:forEach>
						   
						    </select>
						    </div>
						</td>
						
						<!-- importance column -->
						<td>
						    <select 
						    	class="multiselect" 
						    	multiple="multiple"
						    	name="importance_conf_<c:out value="${configuration.id}" />">
						    	
						    	<c:forEach items="${conditionList}" var="timeCondition">
									<option 
										<c:if test="${conditionMode[configuration.id][timeCondition.ordinal] }">selected="selected"</c:if> 
										value="<c:out value="${timeCondition.ordinal}" />">
											<c:out value="${timeCondition.name}" />
									</option>
						        
								</c:forEach>
						   
						    </select>
						    
						
						</td>
						
						<!-- shift preferences column -->
						<td>
						
						    <select 
						    	class="multiselect"
						    	multiple="multiple"
						    	name="shift_conf_<c:out value="${configuration.id}" />">
						    	
						    	<c:forEach items="${shiftConditionList}" var="timeCondition">
									<option 
										<c:if test="${conditionMode[configuration.id][timeCondition.ordinal] }">selected="selected"</c:if> 
										value="<c:out value="${timeCondition.ordinal}" />">
											<c:out value="${timeCondition.name}" />
									</option>
						        
								</c:forEach>

						    </select>
							

						</td>
						
						<!-- time preferences column -->
						<td>
						
							<select 
						    	
						    	class="multiselect"
						    	multiple="multiple"
						    	name="time_conf_<c:out value="${configuration.id}" />">
						    	
						    	<c:forEach items="${timeConditionList}" var="timeCondition">
									<option 
										<c:if test="${conditionMode[configuration.id][timeCondition.ordinal] }">selected="selected"</c:if> 
										value="<c:out value="${timeCondition.ordinal}" />">
											<c:out value="${timeCondition.name}" />
									</option>
						        
								</c:forEach>

						    </select>
							

						</td>
						
						<!-- channel column -->
						<td>
						
						
						<select 
							name="channel_conf_<c:out value="${configuration.id}" />" 
						    	class="multiselect" 
							multiple="multiple">
							
							<c:forEach items="${channelList}" var="channel">
								<option 
									<c:if test="${channelMode[configuration.id][channel.ordinal] }">selected="selected"</c:if> 
									value="<c:out value="${channel.ordinal}" />">
										<c:out value="${channel.name}" />
								</option>
						        
							</c:forEach>
							
						    </select>

						</td>
						<td>
	              		<button class="btn btn-danger btn-remove" type="button">
	                                <span class="fa fa-remove"></span>
	                	</button>
						</td>
					</tr>       
				</c:forEach>
			</table>
			
            	<button class="btn btn-success btn-add " type="button">
                	<span class="fa fa-plus" ></span><span> Add entry</span>
                </button>
		    
		    <button type="submit" class="btn btn-success" ><span class="fa fa-save" ></span> Save</button>
			</form>
			</div></div>
			
            
            


    						    <script type="text/javascript">
    						    $(document).ready(function () 
    						    		{
    						    		var new_id = 0;
    						    		    $(document).on('click', '.btn-add', function(e)
    						    		    {
    						    		        e.preventDefault();
    						    		        var controlForm = $('.controls form:first');
    						    		        var controlForm2 = $('.form-table');
    						    		        //window.alert (controlForm);
    						    		        var currentEntry = $('.entry:first');
    						    		        
    						    		        
    						    		        
    						    		        // tu zamiast kopiowac cos co moze byc zmodyfikowane lub usuniete lepiej dac ukryty pusty wpis
    						    		        var newEntry = $(currentEntry.clone()).appendTo(controlForm2);
    						    		        newEntry.find('input').val('');
    						    		        
    						    		        
    						    		        
    						    		        controlForm.find('.entry:not(:last) .btn-remove')
    						    		            .html('<span class="fa fa-remove"></span>');
    						    		        
    						    		        // make row multiselect work
    						    		        newEntry.find('.multiselect_blank').removeClass('multiselect_blank').addClass('multiselect');
    						    		        newEntry.find('.multiselect').multiselect({numberDisplayed: 1});

    						    		        // unhide whole row
												newEntry.removeClass("hidden");
    						    		        
    						    		        var new_ident = "new_" + new_id;
    						    		        // set row hidden identificators
    						    		        var hiddenId = newEntry.find(".hidden_input_id");
    						    		        hiddenId.attr("name","entry[]");
    						    		        hiddenId.val(new_ident);
												hiddenId.addClass("hidden");// hide identificator
												

												var req = newEntry.find("select[name='event_conf_blank']");
												req.attr("name","event_conf_" + new_ident);
												newEntry.find("select[name='importance_conf_blank']").attr("name","importance_conf_" + new_ident);
												newEntry.find("select[name='shift_conf_blank']").attr("name","shift_conf_" + new_ident);
												newEntry.find("select[name='time_conf_blank']").attr("name","time_conf_" + new_ident);
												newEntry.find("select[name='channel_conf_blank']").attr("name","channel_conf_" + new_ident);
												
												
												
												new_id++;
    						    		    })
    						    		    .on('click', '.btn-remove', function(e)
    						    		    {
    						    		  	$(this).parents('.entry:first').remove();

    						    				e.preventDefault();
    						    				return false;
    						    			});
    						    		});
    						    
    						    
    						    $(document).ready(function () {
    						            $(".multiselect").multiselect({numberDisplayed: 1});
    						            
    						            /* $('#confform').submit(function(){
    						            	console.log("submitting...");
    						                var options = $("select[name='event_conf_new_0']");
    						                console.log("options...");
    						                console.log(options);
    						                console.log("option vals...");
    						                if(options.val() == null ||options.val().length == 0){
    						                    alert('no value selected');
    						                    
    						                    return false;
    						                } 
    						           });*/
    						            
    						    });


							</script>
    </jsp:body>
</t:genericpage>