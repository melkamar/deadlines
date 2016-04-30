/*
 * Copyright (c) 2016 Martin Melka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.melkamar.deadlines.model.offer;

import com.fasterxml.jackson.annotation.JsonView;
import com.melkamar.deadlines.controllers.JsonViews;
import com.melkamar.deadlines.model.User;

import javax.persistence.*;

/**
 * @author Martin Melka
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Offer {
    public static final String COL_OFFERER_ID = "OFFERER_ID";

    @Id
    @Column(name = "OFFER_ID")
    @GeneratedValue(strategy = GenerationType.TABLE)
    @JsonView(JsonViews.Offer.Basic.class)
    protected Long id;

    @ManyToOne
    @JoinColumn(name = COL_OFFERER_ID, referencedColumnName = User.COL_USER_ID)
    @JsonView(JsonViews.Offer.Basic.class)
    final protected User offerer;

    public Offer(User offerer) {
        this.offerer = offerer;
    }

    public Offer() {
        this.offerer = null;
    }

    public User getOfferer() {
        return offerer;
    }

    public Long getId() {
        return id;
    }
}
