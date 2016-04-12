package com.melkamar.deadlines.controllers;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 10.04.2016 10:56
 */
public interface JsonViews {
    public static interface Always {
    }

    /**
     *
     */
    public static interface Group {
        public static interface Basic extends Always {
        }

        public static interface Details extends Basic {
        }

        public static interface AdminInfo {
        }
    }


    public static interface User {
        public static interface Minimal extends Always {
        }

        public static interface Basic extends Minimal {
        }

        public static interface Detail extends Basic {
        }
    }

    /**
     *
     */
    public static interface Task {
        public static interface Minimal extends Always {
        }

        public static interface Basic extends Minimal {
        }

        public static interface Detail extends Basic {
        }
    }

    public static interface TaskParticipant {
        public static interface ShowTaskId extends Always {
        }

        public static interface ShowTaskName extends Always {
        }

        public static interface Basic extends Always {
        }
    }

    public static interface GroupMember {
        public static interface Basic extends Always {
        }
    }

    /**
     *
     */
    public static interface Offer {
        public static interface Basic extends Always {
        }
    }

    /**
     * ***************************************************************
     */
    public static interface Controller {
        public static interface OfferList extends Offer.Basic, Task.Minimal, User.Basic, Group.Basic {
        }

        public static interface GroupList extends User.Basic, Group.Basic, Group.AdminInfo {
        }

        public static interface GroupDetails extends User.Basic, Group.Details, Task.Minimal, TaskParticipant.Basic, GroupMember.Basic,
                TaskParticipant.ShowTaskId, TaskParticipant.ShowTaskName {
        }

        public static interface TaskList extends Task.Basic {
        }

        public static interface TaskDetails extends Task.Detail, User.Minimal, Group.Basic, TaskParticipant.Basic {
        }

    }
}
