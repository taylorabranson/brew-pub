package org.launchcode.brewpub.controllers;

import org.launchcode.brewpub.models.Pub;
import org.launchcode.brewpub.models.data.PubData;
import org.launchcode.brewpub.models.data.PubRepository;
import org.launchcode.brewpub.models.Brew;
import org.launchcode.brewpub.models.data.BrewData;
import org.launchcode.brewpub.models.data.BrewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("search")
public class SearchController {

    @Autowired
    private PubRepository pubRepository;

    @Autowired
    private BrewRepository brewRepository;

    @RequestMapping("")
    public String search(Model model) {
        return "search";
    }

    @GetMapping("results")
    public String redirectToSearch() {
        return "redirect:/search";
    }

    @PostMapping("results")
    public String displaySearchResults(Model model, @RequestParam String searchType, @RequestParam String searchTerm, @RequestParam String puborbrew, @RequestParam(required = false) String searchState){
        Iterable<Pub> pubs;
        Iterable<Brew> brews;
        if (searchType.toLowerCase().equals("state")) {
            pubs = PubData.findByColumnAndValue(searchType, searchState, pubRepository.findAll());
            brews = BrewData.findByColumnAndValue(searchType, searchState, brewRepository.findAll());
            model.addAttribute("pubs", pubs);
            //model.addAttribute("brews", brews);
        } else if (searchTerm.toLowerCase().equals("all") || searchTerm.equals("")){
            pubs = pubRepository.findAll();
            brews = brewRepository.findAll();
        } else {
            pubs = PubData.findByColumnAndValue(searchType, searchTerm, pubRepository.findAll());
            brews = BrewData.findByColumnAndValue(searchType, searchTerm, brewRepository.findAll());
        }

        if (searchState == null) {
            searchState = " ";
        }
        model.addAttribute("title", "Results with " + puborbrew + " - " + searchType + ": " + searchTerm + searchState);

        if (puborbrew.toLowerCase().equals("pub")){
            model.addAttribute("pubs", pubs);
        } else if (puborbrew.toLowerCase().equals("brew")){
            model.addAttribute("brews", brews);
        } else {
            model.addAttribute("pubs", pubs);
            model.addAttribute("brews", brews);
        }

        if (!pubs.iterator().hasNext() && !brews.iterator().hasNext()) {
            model.addAttribute("resultsMessage", "No Results");
        }

        return "search";
    }


}
