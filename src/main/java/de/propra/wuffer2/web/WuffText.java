package de.propra.wuffer2.web;

import javax.validation.constraints.Size;

public record WuffText(
//    @Length(min = 1, max = 300, message = "Wufftext zu lang (maximal 300 Zeichen)")
    @Size(min = 1, max = 300, message = "Wufftext muss zwischen 1 und 300 Zeichen lang sein") String text
) {

}