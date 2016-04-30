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

package com.melkamar.deadlines.controllers;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 10.04.2016 10:56
 */
public interface JsonViews {
    interface Always {
    }

    /**
     *
     */
    interface Group {
        interface Basic extends Always {
        }

        interface Details extends Basic {
        }

        interface AdminInfo {
        }
    }


    interface User {
        interface Minimal extends Always {
        }

        interface Basic extends Minimal {
        }

        interface Detail extends Basic {
        }
    }

    /**
     *
     */
    interface Task {
        interface Minimal extends Always {
        }

        interface Basic extends Minimal {
        }

        interface Detail extends Basic {
        }
    }

    interface TaskParticipant {
        interface ShowTaskId extends Always {
        }

        interface ShowTaskName extends Always {
        }

        interface Basic extends Always {
        }
    }

    interface GroupMember {
        interface Basic extends Always {
        }
    }

    /**
     *
     */
    interface Offer {
        interface Basic extends Always {
        }
    }

    /**
     * ***************************************************************
     */
    interface Controller {
        interface OfferList extends Offer.Basic, Task.Minimal, User.Basic, Group.Basic {
        }

        interface GroupList extends User.Basic, Group.Basic, Group.AdminInfo {
        }

        interface GroupDetails extends User.Basic, Group.Details, Task.Minimal, TaskParticipant.Basic, GroupMember.Basic,
                TaskParticipant.ShowTaskId, TaskParticipant.ShowTaskName {
        }

        interface TaskList extends Task.Basic {
        }

        interface TaskDetails extends Task.Detail, User.Minimal, Group.Basic, TaskParticipant.Basic {
        }

    }
}
