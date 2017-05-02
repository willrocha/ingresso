package br.com.caelum.ingresso.controller;

import br.com.caelum.ingresso.dao.FilmeDao;
import br.com.caelum.ingresso.dao.SalaDao;
import br.com.caelum.ingresso.dao.SessaoDao;
import br.com.caelum.ingresso.model.Sessao;
import br.com.caelum.ingresso.model.SessaoForm;
import br.com.caelum.ingresso.validation.GerenciadorDeSessao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by nando on 03/03/17.
 */
@Controller
public class SessaoController {


    @Autowired
    private SalaDao salaDao;

    @Autowired
    private FilmeDao filmeDao;

    @Autowired
    private SessaoDao sessaoDao;

    @GetMapping("/sessao")
    public ModelAndView form(@RequestParam("salaId") Integer salaId, SessaoForm form) {
        ModelAndView modelAndView = new ModelAndView("sessao/sessao");

        form.setSalaId(salaId);

        modelAndView.addObject("sala", salaDao.findOne(salaId));
        modelAndView.addObject("filmes", filmeDao.findAll());
        modelAndView.addObject("form", form);


        return modelAndView;
    }


    @PostMapping("/sessao")
    @Transactional
    public ModelAndView salva(@Valid SessaoForm form, BindingResult result) {

        if (result.hasErrors()) return form(form.getSalaId(), form);


        Sessao sessao = form.toSessao(salaDao, filmeDao);

        List<Sessao> sessoesDaSala = sessaoDao.buscaSessoesDaSala(sessao.getSala());

        GerenciadorDeSessao gerenciador = new GerenciadorDeSessao(sessoesDaSala);

        if (gerenciador.cabe(sessao)) {
            sessaoDao.save(sessao);
            return new ModelAndView("redirect:/sala/" + form.getSalaId() + "/sessoes");
        }

        return form(form.getSalaId(), form);
    }

}
