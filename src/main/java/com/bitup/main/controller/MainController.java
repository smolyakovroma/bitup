package com.bitup.main.controller;

import com.bitup.main.domain.User;
import com.bitup.main.service.MailService;
import com.bitup.main.service.UserService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@Controller
@RequestMapping
@SessionAttributes("user")
public class MainController {


    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;

    @ModelAttribute(name = "user")
    public User user() {
        return new User();
    }

    @RequestMapping(value = {"/","/index"})
    public ModelAndView main() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }


    public Pair<String, Boolean> getBitcoinCurrency(String coin) {

        String result = " 0.0$";
        Boolean rise = true;
        try {
            URL url = null;
            if ("BTC".equals(coin)) {
                url = new URL("https://api.coinmarketcap.com/v1/ticker/bitcoin/?convert=USD");
            } else if ("ETH".equals(coin)) {
                url = new URL("https://api.coinmarketcap.com/v1/ticker/ethereum/?convert=USD");
            } else {
                return new Pair<>((coin + result), rise);
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + conn.getResponseCode());
                return new Pair<>((coin + result), rise);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                if (output.indexOf("price_usd") > 0) {
                    result = output.substring(output.indexOf(":") + 2);
                    result = result.substring(1, result.length() - 3) + "$";
                }
                if (output.indexOf("percent_change_24h") > 0) {
                    String res = output.substring(output.indexOf(":") + 2);
                    result = result + " (" + res.substring(1, res.length() - 3) + "%)";
                }
                if (output.indexOf("percent_change_24h") > 0) {
                    rise = (output.indexOf("-") > 0 ? false : true);
                }
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pair<>((coin + " " + result), rise);
    }

    public float getCurrency() {
        float result = 0.0f;
        URL url = null;
        try {
            url = new URL("https://query.yahooapis.com/v1/public/yql?q=select+*+from+yahoo.finance.xchange+where+pair+=+%22USDRUB%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + conn.getResponseCode());
                return result;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                if (output.indexOf("Rate") > 0) {
                    result = Float.parseFloat(output.substring(output.indexOf("Rate") + 7, output.indexOf("Rate") + 14));
                }

            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return result;
    }

    private User getPrincipalUser() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findUserByEmail(principal.getUsername());
    }


    ModelAndView getCurrencyForPage(ModelAndView modelAndView) {
        Pair<String, Boolean> btc = getBitcoinCurrency("BTC");
        modelAndView.getModel().put("btc", btc.getKey());
        modelAndView.getModel().put("btc_rise", btc.getValue());
        Pair<String, Boolean> eth = getBitcoinCurrency("ETH");
        modelAndView.getModel().put("eth", eth.getKey());
        modelAndView.getModel().put("eth_rise", eth.getValue());
        return modelAndView;
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public ModelAndView dashboard(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        User user = getPrincipalUser();
        modelAndView.getModel().put("user", user);
        modelAndView = getCurrencyForPage(modelAndView);
        if (user.getTypeClient() == 0) {
            modelAndView.setViewName("redirect:/cabinet/invest_calc");
        } else {
            modelAndView.setViewName("dashboard");
        }

        return modelAndView;
    }

    @RequestMapping(value = "/investment", method = RequestMethod.GET)
    public ModelAndView investment(@SessionAttribute User user) {
        if (user == null) user = getPrincipalUser();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModel().put("user", user);
        modelAndView = getCurrencyForPage(modelAndView);
        modelAndView.setViewName("investment");
        return modelAndView;
    }

    @RequestMapping(value = "/deposit", method = RequestMethod.GET)
    public ModelAndView deposit(@SessionAttribute User user) {
        if (user == null) user = getPrincipalUser();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModel().put("user", user);
        modelAndView = getCurrencyForPage(modelAndView);
        modelAndView.setViewName("deposit");
        return modelAndView;
    }

    @RequestMapping(value = "/withdraw", method = RequestMethod.GET)
    public ModelAndView withdraw(@SessionAttribute User user) {
        if (user == null) user = getPrincipalUser();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModel().put("user", user);
        modelAndView = getCurrencyForPage(modelAndView);
        modelAndView.setViewName("withdraw");
        return modelAndView;
    }


    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    public ModelAndView transactions(@SessionAttribute User user) {
        if (user == null) user = getPrincipalUser();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModel().put("user", user);
        modelAndView = getCurrencyForPage(modelAndView);
        modelAndView.setViewName("transactions");
        return modelAndView;
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public ModelAndView settings(@SessionAttribute User user) {
        if (user == null) user = getPrincipalUser();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModel().put("user", user);
        modelAndView = getCurrencyForPage(modelAndView);
        modelAndView.setViewName("settings");
        return modelAndView;
    }

    @RequestMapping(value = "/accessDenied")
    public String accessDenied() {
        return "accessDenied";
    }

//    @RequestMapping(value = "/error")
//    public String error() {
//        return "error";
//    }


    @RequestMapping(value = "/sendMail", method = RequestMethod.POST)
    public String sendMail(@RequestParam("name") String name, @RequestParam("mail") String mail, @RequestParam("tel") String tel, @RequestParam("priority") String priority) {
//        ResponseEntity<String>
        StringBuilder builder = new StringBuilder();
        builder.append("<table cellpadding=10 style=\"margin-top:10px; margin-left:10px;\" border=\"1\">");
        builder.append("<tr><td>Имя</td><td>"+name+"</td></tr>");
        builder.append("<tr><td >Почта</td><td>"+mail+"</td></tr>");
        builder.append("<tr><td>Телефон</td><td>"+tel+"</td></tr>");
        builder.append("<tr><td>Приоритетный способ связи</td><td>"+priority+"</td></tr></table>");
        mailService.send(name, "info@hashfactory.ru", "Заявка", builder.toString());

        builder = new StringBuilder();

        builder.append("<table cellpadding=15 style='margin-top:10px; margin-left:20px;' border='0'>");
        builder.append("<tr><td align=center  colspan='2'><a href='www.hashfactory.ru'>logo</a></td></tr>");
        builder.append("<tr><td align=center ><br/><h2>Приветствуем!</h2>");
        builder.append("<h3>В ближайшее время мы с вами свяжемся!</h3><br/></td>");
        builder.append("<td align=center>team2</td></tr>");
        builder.append("<tr><td></td><td align=right><p>Служба поддежки <a href='mailto:admin@hashactory.ru'>admin@hashactory.ru</a></p>");
        builder.append("<p>Телефон 8 800 100 5979 (по России бесплатно)</p></td></tr></table>");

        mailService.send(name, mail, "Мы с вами свяжемся!", builder.toString());


        return "redirect:/";
    }


    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public ModelAndView registration(@RequestParam String email, @RequestParam(defaultValue = "false", required = false) boolean recovery) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findUserByEmail(email.trim());

        if (recovery){
            user.setActive(0);
            userService.saveUser(user);
        }

        if (user == null || user.getActive() != 0) {
            modelAndView.setViewName("/");
            return modelAndView;
        }



        modelAndView.setViewName("registration");
        modelAndView.addObject("email", email);
        return modelAndView;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView registrationCompleted(@RequestParam String email, @RequestParam String name, @RequestParam String password, @RequestParam String password2) {
        ModelAndView modelAndView = new ModelAndView();
        if (password.equals(password2) & !name.trim().isEmpty()) {
            User user = userService.findUserByEmail(email.trim());
            user.setName(name);
            user.setPassword(password.trim());
            user.setActive(1);
            userService.saveNewUser(user);
            modelAndView.setViewName("/");
            return modelAndView;
        }

        if (!password.equals(password2)) {
            modelAndView.addObject("error", "Пароли должны совпадать");
        } else if (name.trim().isEmpty()) {
            modelAndView.addObject("error", "Заполните имя");
        }
        modelAndView.addObject("name", name.trim());
        modelAndView.addObject("email", email.trim());
        modelAndView.setViewName("registration");

        return modelAndView;
    }


    @RequestMapping(value = "/recovery", method = RequestMethod.GET)
    public ModelAndView recovery() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("recovery");
        return modelAndView;
    }

    @RequestMapping(value = "/recovery", method = RequestMethod.POST)
    public ModelAndView recoveryCompleted(@RequestParam String email) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findUserByEmail(email.trim());

        if (user!=null && user.getActive()!=0) {
            StringBuilder builder = new StringBuilder();

            builder.append("<table cellpadding=15 style='margin-top:10px; margin-left:20px;' border='0'>");
            builder.append("<tr><td align=center  colspan='2'><a href='www.hashfactory.ru'>logo</a></td></tr>");
            builder.append("<tr><td align=center ><br/><h2>Восстановление пароля</h2>");
            builder.append("<h3>Вы сделали запрос на восстановление пароля к личному кабинету</h3><br/>");
            builder.append("<h3>Для продолжения процедуры <a href='www.hashfactory.ru/registration?email=" + email.trim() + "&recovery=true'>Перейти</a></h3><br/>");
            builder.append("<h3>Если вы этого неделали, проигнорируйте письмо</h3><br/></td>");
            builder.append("<td align=center>team4</td></tr>");
            builder.append("<tr><td></td><td align=right><p>Служба поддежки <a href='mailto:admin@hashactory.ru'>admin@hashactory.ru</a></p>");
            builder.append("<p>Телефон 8 800 100 5979 (по России бесплатно)</p></td></tr></table>");

            mailService.send(null, user.getEmail(), "Запрос на восстановление пароля", builder.toString());

            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }


        modelAndView.addObject("error", "E-mail не найден!");

        modelAndView.setViewName("recovery");

        return modelAndView;
    }



}
