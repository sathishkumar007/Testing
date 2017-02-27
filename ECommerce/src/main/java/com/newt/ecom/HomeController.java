package com.newt.ecom;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.newt.ecom.bean.ProductCheckoutDetails;
import com.newt.ecom.bean.ProductList;
import com.newt.ecom.bean.ShoppingBean;
import com.newt.ecom.commonutils.Productstatus;
import com.newt.ecom.model.Customer;
import com.newt.ecom.model.Product;
import com.newt.ecom.model.ShoppingCartItems;
import com.newt.model.ProductElasticSearch;

/**
 * Handles requests for the application home page.
 */
@SessionAttributes("loggedInProfile")
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value = "/loginPage", method = RequestMethod.GET)
	public ModelAndView getHome(@ModelAttribute("shoppingBean") ShoppingBean shoppingBean) {
        ModelAndView modelAndView= new ModelAndView("loginPage");
			shoppingBean.setProductList(getProductList());
			modelAndView.addObject("productList",shoppingBean.getProductList());
			modelAndView.addObject("customer", new Customer());
		    modelAndView.addObject("loggedInProfile", new Customer());
		return modelAndView;
	}
	
	@RequestMapping(value = "/customerRegisterPage", method = RequestMethod.GET)
	public ModelAndView customerHome() {
        ModelAndView modelAndView= new ModelAndView("customerRegisterPage");
		 modelAndView.addObject("customer", new Customer());
	     modelAndView.addObject("loggedInProfile", new Customer());
		return modelAndView;
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView registerCustomer(@ModelAttribute("customer") Customer customer,@ModelAttribute("shoppingBean") ShoppingBean shoppingBean, 
			BindingResult result) {
		
		customer.setCustomerName(customer.getFirstName()+""+customer.getLastName());
		customer.setUsername(customer.getCustomerEmail());
		customer.setStatus("ACTIVE");
		
		logger.info("Customer Info ====>"+customer.toString());
		
		ModelAndView modelAndView=new ModelAndView("loginPage");
		//final String restURI = "http://52.207.17.79:8767/customer/add";	
		final String restURI = "http://localhost:8767/customer/add";
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		
		Customer cust1 = restTemplate.postForObject( restURI, customer, Customer.class);
		if(cust1 != null){
			modelAndView.addObject("successMsg", "Customer registred successfuly");
			shoppingBean.setProductList(getProductList());
			modelAndView.addObject("productList",shoppingBean.getProductList());
		}		
		return modelAndView;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView loginCustomer(@ModelAttribute("customer") Customer customer, @ModelAttribute("shoppingBean") ShoppingBean shoppingBean) {
        ModelAndView modelAndView;
      //Validation
        logger.info("Customer Info====>"+customer.getUsername());
    	if(customer.getUsername()=="" || customer.getPassword()==""){
    		if(customer.getUsername()=="" && customer.getPassword()==""){
    	        modelAndView=new ModelAndView("invalidLogin");
    	        modelAndView.addObject("errorMsg", "please enter the username and password");

    	        return modelAndView;
    		}
    		if(customer.getUsername()==""){
    	        modelAndView=new ModelAndView("invalidLogin");
    	    	modelAndView.addObject("userNameMsg", "please enter the username");
    	        return modelAndView;

    		}
    		if(customer.getPassword()==""){
    	        modelAndView=new ModelAndView("invalidLogin");
    	        modelAndView.addObject("passwordMsg", "please enter the password");
    	        return modelAndView;
    		}
    		
    	}else{

	        final String restURI = "http://localhost:8767/customer/login/"+customer.getUsername();	
	        RestTemplate restTemplate = new RestTemplate();
	        Customer customerDetails = restTemplate.getForObject( restURI, Customer.class);
	        if(customerDetails != null){
	        	if(!customerDetails.getPassword().equals(customer.getPassword())){
	        		 modelAndView=new ModelAndView("invalidLogin");
		    	     modelAndView.addObject("errorMsg", "Invalid password try again!!");
		    	     return modelAndView;	
	        	}
	        	modelAndView=new ModelAndView("productCatalog");
	        	shoppingBean.setProductList(getProductList());
	 			modelAndView.addObject("productList",shoppingBean.getProductList());
			    modelAndView.addObject("loggedInProfile", customerDetails);
	        	shoppingBean.setCustomerId(customerDetails.getCustomerId());
	    		shoppingBean.setCustomerName(customerDetails.getCustomerName());
	    		shoppingBean.setCustomerEmail(customerDetails.getCustomerEmail());
	    		modelAndView.addObject("shoppingBean", shoppingBean);
	    		return modelAndView;	
	        	
	        	
	        }else{
	        	 modelAndView=new ModelAndView("invalidLogin");
	    	     modelAndView.addObject("errorMsg", "Invalid Credantials");
	    	     return modelAndView;	
	        }
    	}
    	
   	 modelAndView=new ModelAndView("invalidLogin");
     modelAndView.addObject("errorMsg", "Username and Password does not Match. please enter the correct username and password");
	 modelAndView.addObject("loggedInProfile", new Customer());
     return modelAndView;
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView loadLogoutPage(SessionStatus status, @ModelAttribute("shoppingBean") ShoppingBean shoppingBean) {
		status.setComplete();
		ModelAndView modelAndView= new ModelAndView("loginPage");
       	shoppingBean.setProductList(getProductList());
		modelAndView.addObject("productList",shoppingBean.getProductList());
		modelAndView.addObject("customer", new Customer());
	    modelAndView.addObject("loggedInProfile", new Customer());
	    return modelAndView;
	}
	
		
	@RequestMapping(value = "/addProductCart", method = RequestMethod.POST)
	public ModelAndView addProductToCart(@ModelAttribute("loggedInProfile") Customer loggedInProfile,@ModelAttribute("shoppingBean") ShoppingBean shoppingBean, 
			BindingResult result) {
		ModelAndView modelAndView=new ModelAndView("productCatalog");
		
		logger.debug("=====>"+shoppingBean.getProductId()+shoppingBean.getProductName()+shoppingBean.getProductPrice()+shoppingBean.getProductDescription());
		
		final String restURI = "http://localhost:8766/shoppingcart/OrderService/create/"+
		shoppingBean.getCustomerId()+"/"+shoppingBean.getCustomerName()+"/"+shoppingBean.getProductId()+"/"+shoppingBean.getProductName()
		+"/"+shoppingBean.getProductPrice()+"/"+shoppingBean.getProductDescription();	
		
		RestTemplate restTemplate = new RestTemplate();		
		HashMap<String,String> results = restTemplate.postForObject(restURI, null, HashMap.class);
		logger.debug(results.get("Message"));
		if(results != null){
			modelAndView.addObject("message", "Product added to the cart successfully!!!");
		}
    	shoppingBean.setProductList(getProductList());
		shoppingBean.setTotalPrice(Float.parseFloat(results.get("MESSAGE")));
		shoppingBean.setCustomerId(shoppingBean.getCustomerId());
		shoppingBean.setCustomerName(shoppingBean.getCustomerName());
		modelAndView.addObject("productList",shoppingBean.getProductList());
	    modelAndView.addObject("loggedInProfile", loggedInProfile);
		modelAndView.addObject("shoppingBean", shoppingBean);
		return modelAndView;
	}
	
	@RequestMapping(value = "/proceedToPay", method = RequestMethod.GET)
	public ModelAndView proceedToPay(@ModelAttribute("loggedInProfile") Customer loggedInProfile,@ModelAttribute("shoppingBean") ShoppingBean shoppingBean) {
		ModelAndView modelAndView=new ModelAndView("ProceedToPay");
		logger.debug("=====>"+shoppingBean.getProductId()+shoppingBean.getProductName()+shoppingBean.getProductPrice()+shoppingBean.getProductDescription());
		
		final String restURI = "http://localhost:8766/shoppingcart/OrderService/get/productList/"+shoppingBean.getCustomerId();		
		RestTemplate restTemplate = new RestTemplate();
		List<ShoppingCartItems> results = restTemplate.getForObject(restURI, List.class);
		logger.debug("=====>"+results);
		
		shoppingBean.setShoppingCartItems(results);
		shoppingBean.setCustomerId(shoppingBean.getCustomerId());
		shoppingBean.setCustomerName(shoppingBean.getCustomerName());
		modelAndView.addObject("loggedInProfile", loggedInProfile);
		modelAndView.addObject("shoppingBean", shoppingBean);
		return modelAndView;
	}
	
	@RequestMapping(value = "/makepayment", method = RequestMethod.POST)
	public ModelAndView makePayment(@ModelAttribute("shoppingBean") ShoppingBean shoppingBean, 
			BindingResult result) {
		ModelAndView modelAndView=new ModelAndView("orderCreated");
		System.out.println("=====>***"+shoppingBean.toString());
		ProductCheckoutDetails productCheckout = new ProductCheckoutDetails();
		productCheckout.setCustomerId(shoppingBean.getCustomerId());
		productCheckout.setCustomerName(shoppingBean.getCustomerName());
		productCheckout.setAddress1(shoppingBean.getAddress1());
		productCheckout.setAddress2(shoppingBean.getAddress2());
		productCheckout.setCity(shoppingBean.getCity());
		productCheckout.setState(shoppingBean.getState());
		productCheckout.setCountry(shoppingBean.getCountry());
		productCheckout.setCardExpDate(new Date());
		productCheckout.setCreditCardNo(shoppingBean.getCreditCardNo());
		productCheckout.setPincode(shoppingBean.getPincode());
		List<ProductList> lists = new ArrayList<ProductList>();
		for(ShoppingCartItems shoppingCartList : shoppingBean.getShoppingCartItems()){
			ProductList productList = new ProductList();
			productList.setCustomerId(shoppingBean.getCustomerId());
			productList.setProductId(shoppingCartList.getProductId());
			productList.setProductName(shoppingCartList.getProductName());
			productList.setProductStatus(Productstatus.SHIPPED);
			productList.setShoppingcartId(shoppingCartList.getShoppingcartId());
			System.out.println("shopping cart ID"+shoppingCartList.getShoppingcartId());
			lists.add(productList);
		}
		productCheckout.setProductList(lists);
		logger.info("productCheckout==>"+productCheckout.toString());
		final String restURI = "http://localhost:8764/checkout/addCheckoutDetails/";		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		ProductCheckoutDetails results = restTemplate.postForObject(restURI, productCheckout, ProductCheckoutDetails.class);
		if(results != null){
			modelAndView.addObject("message", "Order Created Successfully!!!!");
		}		
		return modelAndView;
	}
	
	@RequestMapping(value = "/getTags", method = RequestMethod.GET)
	public @ResponseBody List<ProductElasticSearch> getTags(@RequestParam String productName) {
		logger.debug("product entering"+ productName);
		List<ProductElasticSearch> result=new ArrayList<ProductElasticSearch>();
		List<ProductElasticSearch> dataList= getProductList();
		for (ProductElasticSearch productBean : dataList) {
			if(null!= productBean.getProductName()){
				if(productBean.getProductName().toUpperCase().contains(productName.toUpperCase())){
					result.add(productBean);
				}
			}	
		}
		return result;

	}
	
	private List<ProductElasticSearch> getProductList(){
		 final String restURI = "http://localhost:8765/products/";		
		 RestTemplate restTemplate = new RestTemplate();
		 List<ProductElasticSearch> results = restTemplate.getForObject(restURI, List.class);
		 if(results != null)
			 return results;
		 return null;
	}
}
