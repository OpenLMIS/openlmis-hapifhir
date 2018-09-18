

CREATE TABLE hfj_forced_id (
    pid bigint NOT NULL,
    forced_id character varying(100) NOT NULL,
    resource_pid bigint NOT NULL,
    resource_type character varying(100) DEFAULT ''::character varying
);


ALTER TABLE hfj_forced_id OWNER TO postgres;

--
-- Name: hfj_history_tag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_history_tag (
    pid bigint NOT NULL,
    tag_id bigint,
    res_id bigint NOT NULL,
    res_type character varying(30) NOT NULL,
    res_ver_pid bigint NOT NULL
);


ALTER TABLE hfj_history_tag OWNER TO postgres;

--
-- Name: hfj_idx_cmp_string_uniq; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_idx_cmp_string_uniq (
    pid bigint NOT NULL,
    idx_string character varying(150) NOT NULL,
    res_id bigint
);


ALTER TABLE hfj_idx_cmp_string_uniq OWNER TO postgres;

--
-- Name: hfj_res_link; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_res_link (
    pid bigint NOT NULL,
    src_path character varying(100) NOT NULL,
    src_resource_id bigint NOT NULL,
    source_resource_type character varying(30) DEFAULT ''::character varying NOT NULL,
    target_resource_id bigint,
    target_resource_type character varying(30) DEFAULT ''::character varying NOT NULL,
    target_resource_url character varying(200),
    sp_updated timestamp without time zone
);


ALTER TABLE hfj_res_link OWNER TO postgres;

--
-- Name: hfj_res_param_present; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_res_param_present (
    pid bigint NOT NULL,
    sp_present boolean NOT NULL,
    res_id bigint NOT NULL,
    sp_id bigint NOT NULL
);


ALTER TABLE hfj_res_param_present OWNER TO postgres;

--
-- Name: hfj_res_tag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_res_tag (
    pid bigint NOT NULL,
    tag_id bigint,
    res_id bigint,
    res_type character varying(30) NOT NULL
);


ALTER TABLE hfj_res_tag OWNER TO postgres;

--
-- Name: hfj_res_ver; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_res_ver (
    pid bigint NOT NULL,
    res_deleted_at timestamp without time zone,
    res_version character varying(7),
    has_tags boolean NOT NULL,
    res_published timestamp without time zone NOT NULL,
    res_updated timestamp without time zone NOT NULL,
    res_encoding character varying(5) NOT NULL,
    res_text oid,
    res_id bigint,
    res_type character varying(30) NOT NULL,
    res_ver bigint NOT NULL,
    forced_id_pid bigint
);


ALTER TABLE hfj_res_ver OWNER TO postgres;

--
-- Name: hfj_resource; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_resource (
    res_id bigint NOT NULL,
    res_deleted_at timestamp without time zone,
    res_version character varying(7),
    has_tags boolean NOT NULL,
    res_published timestamp without time zone NOT NULL,
    res_updated timestamp without time zone NOT NULL,
    sp_has_links boolean,
    hash_sha256 character varying(64),
    sp_index_status bigint,
    res_language character varying(20),
    sp_cmpstr_uniq_present boolean,
    sp_coords_present boolean,
    sp_date_present boolean,
    sp_number_present boolean,
    sp_quantity_present boolean,
    sp_string_present boolean,
    sp_token_present boolean,
    sp_uri_present boolean,
    res_profile character varying(200),
    res_type character varying(30),
    res_ver bigint,
    forced_id_pid bigint
);


ALTER TABLE hfj_resource OWNER TO postgres;

--
-- Name: hfj_search; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_search (
    pid bigint NOT NULL,
    created timestamp without time zone NOT NULL,
    failure_code integer,
    failure_message character varying(500),
    last_updated_high timestamp without time zone,
    last_updated_low timestamp without time zone,
    num_found integer NOT NULL,
    preferred_page_size integer,
    resource_id bigint,
    resource_type character varying(200),
    search_last_returned timestamp without time zone NOT NULL,
    search_query_string text,
    search_query_string_hash integer,
    search_type integer NOT NULL,
    search_status character varying(10) NOT NULL,
    total_count integer,
    search_uuid character varying(40) NOT NULL
);


ALTER TABLE hfj_search OWNER TO postgres;

--
-- Name: hfj_search_include; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_search_include (
    pid bigint NOT NULL,
    search_include character varying(200) NOT NULL,
    inc_recurse boolean NOT NULL,
    revinclude boolean NOT NULL,
    search_pid bigint NOT NULL
);


ALTER TABLE hfj_search_include OWNER TO postgres;

--
-- Name: hfj_search_parm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_search_parm (
    pid bigint NOT NULL,
    param_name character varying(100) NOT NULL,
    res_type character varying(30) NOT NULL
);


ALTER TABLE hfj_search_parm OWNER TO postgres;

--
-- Name: hfj_search_result; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_search_result (
    pid bigint NOT NULL,
    search_order integer NOT NULL,
    resource_pid bigint NOT NULL,
    search_pid bigint NOT NULL
);


ALTER TABLE hfj_search_result OWNER TO postgres;

--
-- Name: hfj_spidx_coords; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_spidx_coords (
    sp_id bigint NOT NULL,
    sp_missing boolean,
    sp_name character varying(100) NOT NULL,
    res_id bigint,
    res_type character varying(255) NOT NULL,
    sp_updated timestamp without time zone,
    sp_latitude double precision,
    sp_longitude double precision
);


ALTER TABLE hfj_spidx_coords OWNER TO postgres;

--
-- Name: hfj_spidx_date; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_spidx_date (
    sp_id bigint NOT NULL,
    sp_missing boolean,
    sp_name character varying(100) NOT NULL,
    res_id bigint,
    res_type character varying(255) NOT NULL,
    sp_updated timestamp without time zone,
    sp_value_high timestamp without time zone,
    sp_value_low timestamp without time zone
);


ALTER TABLE hfj_spidx_date OWNER TO postgres;

--
-- Name: hfj_spidx_number; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_spidx_number (
    sp_id bigint NOT NULL,
    sp_missing boolean,
    sp_name character varying(100) NOT NULL,
    res_id bigint,
    res_type character varying(255) NOT NULL,
    sp_updated timestamp without time zone,
    sp_value numeric(19,2)
);


ALTER TABLE hfj_spidx_number OWNER TO postgres;

--
-- Name: hfj_spidx_quantity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_spidx_quantity (
    sp_id bigint NOT NULL,
    sp_missing boolean,
    sp_name character varying(100) NOT NULL,
    res_id bigint,
    res_type character varying(255) NOT NULL,
    sp_updated timestamp without time zone,
    hash_units_and_valprefix bigint,
    hash_valprefix bigint,
    sp_system character varying(200),
    sp_units character varying(200),
    sp_value numeric(19,2)
);


ALTER TABLE hfj_spidx_quantity OWNER TO postgres;

--
-- Name: hfj_spidx_string; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_spidx_string (
    sp_id bigint NOT NULL,
    sp_missing boolean,
    sp_name character varying(100) NOT NULL,
    res_id bigint,
    res_type character varying(255) NOT NULL,
    sp_updated timestamp without time zone,
    hash_exact bigint,
    hash_norm_prefix bigint,
    sp_value_exact character varying(200),
    sp_value_normalized character varying(200)
);


ALTER TABLE hfj_spidx_string OWNER TO postgres;

--
-- Name: hfj_spidx_token; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_spidx_token (
    sp_id bigint NOT NULL,
    sp_missing boolean,
    sp_name character varying(100) NOT NULL,
    res_id bigint,
    res_type character varying(255) NOT NULL,
    sp_updated timestamp without time zone,
    hash_sys bigint,
    hash_sys_and_value bigint,
    hash_value bigint,
    sp_system character varying(200),
    sp_value character varying(200)
);


ALTER TABLE hfj_spidx_token OWNER TO postgres;

--
-- Name: hfj_spidx_uri; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_spidx_uri (
    sp_id bigint NOT NULL,
    sp_missing boolean,
    sp_name character varying(100) NOT NULL,
    res_id bigint,
    res_type character varying(255) NOT NULL,
    sp_updated timestamp without time zone,
    hash_uri bigint,
    sp_uri character varying(255)
);


ALTER TABLE hfj_spidx_uri OWNER TO postgres;

--
-- Name: hfj_subscription_stats; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_subscription_stats (
    pid bigint NOT NULL,
    created_time timestamp without time zone NOT NULL,
    res_id bigint
);


ALTER TABLE hfj_subscription_stats OWNER TO postgres;

--
-- Name: hfj_tag_def; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE hfj_tag_def (
    tag_id bigint NOT NULL,
    tag_code character varying(200),
    tag_display character varying(200),
    tag_system character varying(200),
    tag_type integer NOT NULL
);


ALTER TABLE hfj_tag_def OWNER TO postgres;

--
-- Name: seq_cncpt_map_grp_elm_tgt_pid; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_cncpt_map_grp_elm_tgt_pid
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_cncpt_map_grp_elm_tgt_pid OWNER TO postgres;

--
-- Name: seq_codesystem_pid; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_codesystem_pid
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_codesystem_pid OWNER TO postgres;

--
-- Name: seq_codesystemver_pid; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_codesystemver_pid
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_codesystemver_pid OWNER TO postgres;

--
-- Name: seq_concept_desig_pid; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_concept_desig_pid
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_concept_desig_pid OWNER TO postgres;

--
-- Name: seq_concept_map_group_pid; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_concept_map_group_pid
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_concept_map_group_pid OWNER TO postgres;

--
-- Name: seq_concept_map_grp_elm_pid; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_concept_map_grp_elm_pid
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_concept_map_grp_elm_pid OWNER TO postgres;

--
-- Name: seq_concept_map_pid; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_concept_map_pid
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_concept_map_pid OWNER TO postgres;

--
-- Name: seq_concept_pc_pid; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_concept_pc_pid
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_concept_pc_pid OWNER TO postgres;

--
-- Name: seq_concept_pid; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_concept_pid
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_concept_pid OWNER TO postgres;

--
-- Name: seq_concept_prop_pid; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_concept_prop_pid
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_concept_prop_pid OWNER TO postgres;

--
-- Name: seq_forcedid_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_forcedid_id
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_forcedid_id OWNER TO postgres;

--
-- Name: seq_historytag_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_historytag_id
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_historytag_id OWNER TO postgres;

--
-- Name: seq_idxcmpstruniq_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_idxcmpstruniq_id
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_idxcmpstruniq_id OWNER TO postgres;

--
-- Name: seq_reslink_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_reslink_id
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_reslink_id OWNER TO postgres;

--
-- Name: seq_resource_history_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_resource_history_id
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_resource_history_id OWNER TO postgres;

--
-- Name: seq_resource_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_resource_id
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_resource_id OWNER TO postgres;

--
-- Name: seq_resparmpresent_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_resparmpresent_id
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_resparmpresent_id OWNER TO postgres;

--
-- Name: seq_restag_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_restag_id
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_restag_id OWNER TO postgres;

--
-- Name: seq_search; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_search
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_search OWNER TO postgres;

--
-- Name: seq_search_inc; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_search_inc
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_search_inc OWNER TO postgres;

--
-- Name: seq_search_res; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_search_res
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_search_res OWNER TO postgres;

--
-- Name: seq_searchparm_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_searchparm_id
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_searchparm_id OWNER TO postgres;

--
-- Name: seq_spidx_coords; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_spidx_coords
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_spidx_coords OWNER TO postgres;

--
-- Name: seq_spidx_date; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_spidx_date
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_spidx_date OWNER TO postgres;

--
-- Name: seq_spidx_number; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_spidx_number
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_spidx_number OWNER TO postgres;

--
-- Name: seq_spidx_quantity; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_spidx_quantity
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_spidx_quantity OWNER TO postgres;

--
-- Name: seq_spidx_string; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_spidx_string
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_spidx_string OWNER TO postgres;

--
-- Name: seq_spidx_token; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_spidx_token
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_spidx_token OWNER TO postgres;

--
-- Name: seq_spidx_uri; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_spidx_uri
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_spidx_uri OWNER TO postgres;

--
-- Name: seq_subscription_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_subscription_id
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_subscription_id OWNER TO postgres;

--
-- Name: seq_tagdef_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE seq_tagdef_id
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_tagdef_id OWNER TO postgres;

--
-- Name: trm_codesystem; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE trm_codesystem (
    pid bigint NOT NULL,
    code_system_uri character varying(255) NOT NULL,
    cs_name character varying(255),
    res_id bigint,
    current_version_pid bigint
);


ALTER TABLE trm_codesystem OWNER TO postgres;

--
-- Name: trm_codesystem_ver; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE trm_codesystem_ver (
    pid bigint NOT NULL,
    cs_version_id character varying(255),
    codesystem_pid bigint,
    res_id bigint NOT NULL
);


ALTER TABLE trm_codesystem_ver OWNER TO postgres;

--
-- Name: trm_concept; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE trm_concept (
    pid bigint NOT NULL,
    code character varying(100) NOT NULL,
    codesystem_pid bigint,
    display character varying(400),
    index_status bigint,
    code_sequence integer
);


ALTER TABLE trm_concept OWNER TO postgres;

--
-- Name: trm_concept_desig; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE trm_concept_desig (
    pid bigint NOT NULL,
    lang character varying(500),
    use_code character varying(500),
    use_display character varying(500),
    use_system character varying(500),
    val character varying(500) NOT NULL,
    concept_pid bigint
);


ALTER TABLE trm_concept_desig OWNER TO postgres;

--
-- Name: trm_concept_map; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE trm_concept_map (
    pid bigint NOT NULL,
    res_id bigint,
    source_url character varying(200),
    target_url character varying(200),
    url character varying(200) NOT NULL
);


ALTER TABLE trm_concept_map OWNER TO postgres;

--
-- Name: trm_concept_map_group; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE trm_concept_map_group (
    pid bigint NOT NULL,
    myconceptmapurl character varying(255),
    source_url character varying(200) NOT NULL,
    mysourcevalueset character varying(255),
    source_version character varying(100),
    target_url character varying(200) NOT NULL,
    mytargetvalueset character varying(255),
    target_version character varying(100),
    concept_map_pid bigint NOT NULL
);


ALTER TABLE trm_concept_map_group OWNER TO postgres;

--
-- Name: trm_concept_map_grp_element; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE trm_concept_map_grp_element (
    pid bigint NOT NULL,
    source_code character varying(100) NOT NULL,
    myconceptmapurl character varying(255),
    source_display character varying(400),
    mysystem character varying(255),
    mysystemversion character varying(255),
    myvalueset character varying(255),
    concept_map_group_pid bigint NOT NULL
);


ALTER TABLE trm_concept_map_grp_element OWNER TO postgres;

--
-- Name: trm_concept_map_grp_elm_tgt; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE trm_concept_map_grp_elm_tgt (
    pid bigint NOT NULL,
    target_code character varying(50) NOT NULL,
    myconceptmapurl character varying(255),
    target_display character varying(400),
    target_equivalence character varying(50),
    mysystem character varying(255),
    mysystemversion character varying(255),
    myvalueset character varying(255),
    concept_map_grp_elm_pid bigint NOT NULL
);


ALTER TABLE trm_concept_map_grp_elm_tgt OWNER TO postgres;

--
-- Name: trm_concept_pc_link; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE trm_concept_pc_link (
    pid bigint NOT NULL,
    child_pid bigint,
    parent_pid bigint,
    rel_type integer,
    codesystem_pid bigint NOT NULL
);


ALTER TABLE trm_concept_pc_link OWNER TO postgres;

--
-- Name: trm_concept_property; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE trm_concept_property (
    pid bigint NOT NULL,
    prop_codesystem character varying(500),
    prop_display character varying(500),
    prop_key character varying(500) NOT NULL,
    prop_type integer NOT NULL,
    prop_val character varying(500),
    concept_pid bigint
);


ALTER TABLE trm_concept_property OWNER TO postgres;


--
-- Name: seq_cncpt_map_grp_elm_tgt_pid; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_cncpt_map_grp_elm_tgt_pid', 1, false);


--
-- Name: seq_codesystem_pid; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_codesystem_pid', 1, false);


--
-- Name: seq_codesystemver_pid; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_codesystemver_pid', 1, false);


--
-- Name: seq_concept_desig_pid; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_concept_desig_pid', 1, false);


--
-- Name: seq_concept_map_group_pid; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_concept_map_group_pid', 1, false);


--
-- Name: seq_concept_map_grp_elm_pid; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_concept_map_grp_elm_pid', 1, false);


--
-- Name: seq_concept_map_pid; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_concept_map_pid', 1, false);


--
-- Name: seq_concept_pc_pid; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_concept_pc_pid', 1, false);


--
-- Name: seq_concept_pid; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_concept_pid', 1, false);


--
-- Name: seq_concept_prop_pid; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_concept_prop_pid', 1, false);


--
-- Name: seq_forcedid_id; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_forcedid_id', 1, false);


--
-- Name: seq_historytag_id; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_historytag_id', 1, false);


--
-- Name: seq_idxcmpstruniq_id; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_idxcmpstruniq_id', 1, false);


--
-- Name: seq_reslink_id; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_reslink_id', 1, false);


--
-- Name: seq_resource_history_id; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_resource_history_id', 1, false);


--
-- Name: seq_resource_id; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_resource_id', 1, false);


--
-- Name: seq_resparmpresent_id; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_resparmpresent_id', 1, false);


--
-- Name: seq_restag_id; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_restag_id', 1, false);


--
-- Name: seq_search; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_search', 1, false);


--
-- Name: seq_search_inc; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_search_inc', 1, false);


--
-- Name: seq_search_res; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_search_res', 1, false);


--
-- Name: seq_searchparm_id; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_searchparm_id', 1, false);


--
-- Name: seq_spidx_coords; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_spidx_coords', 1, false);


--
-- Name: seq_spidx_date; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_spidx_date', 1, false);


--
-- Name: seq_spidx_number; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_spidx_number', 1, false);


--
-- Name: seq_spidx_quantity; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_spidx_quantity', 1, false);


--
-- Name: seq_spidx_string; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_spidx_string', 1, false);


--
-- Name: seq_spidx_token; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_spidx_token', 1, false);


--
-- Name: seq_spidx_uri; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_spidx_uri', 1, false);


--
-- Name: seq_subscription_id; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_subscription_id', 1, false);


--
-- Name: seq_tagdef_id; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('seq_tagdef_id', 1, false);


--
-- Name: hfj_forced_id hfj_forced_id_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_forced_id
    ADD CONSTRAINT hfj_forced_id_pkey PRIMARY KEY (pid);


--
-- Name: hfj_history_tag hfj_history_tag_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_history_tag
    ADD CONSTRAINT hfj_history_tag_pkey PRIMARY KEY (pid);


--
-- Name: hfj_idx_cmp_string_uniq hfj_idx_cmp_string_uniq_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_idx_cmp_string_uniq
    ADD CONSTRAINT hfj_idx_cmp_string_uniq_pkey PRIMARY KEY (pid);


--
-- Name: hfj_res_link hfj_res_link_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_link
    ADD CONSTRAINT hfj_res_link_pkey PRIMARY KEY (pid);


--
-- Name: hfj_res_param_present hfj_res_param_present_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_param_present
    ADD CONSTRAINT hfj_res_param_present_pkey PRIMARY KEY (pid);


--
-- Name: hfj_res_tag hfj_res_tag_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_tag
    ADD CONSTRAINT hfj_res_tag_pkey PRIMARY KEY (pid);


--
-- Name: hfj_res_ver hfj_res_ver_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_ver
    ADD CONSTRAINT hfj_res_ver_pkey PRIMARY KEY (pid);


--
-- Name: hfj_resource hfj_resource_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_resource
    ADD CONSTRAINT hfj_resource_pkey PRIMARY KEY (res_id);


--
-- Name: hfj_search_include hfj_search_include_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_search_include
    ADD CONSTRAINT hfj_search_include_pkey PRIMARY KEY (pid);


--
-- Name: hfj_search_parm hfj_search_parm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_search_parm
    ADD CONSTRAINT hfj_search_parm_pkey PRIMARY KEY (pid);


--
-- Name: hfj_search hfj_search_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_search
    ADD CONSTRAINT hfj_search_pkey PRIMARY KEY (pid);


--
-- Name: hfj_search_result hfj_search_result_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_search_result
    ADD CONSTRAINT hfj_search_result_pkey PRIMARY KEY (pid);


--
-- Name: hfj_spidx_coords hfj_spidx_coords_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_coords
    ADD CONSTRAINT hfj_spidx_coords_pkey PRIMARY KEY (sp_id);


--
-- Name: hfj_spidx_date hfj_spidx_date_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_date
    ADD CONSTRAINT hfj_spidx_date_pkey PRIMARY KEY (sp_id);


--
-- Name: hfj_spidx_number hfj_spidx_number_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_number
    ADD CONSTRAINT hfj_spidx_number_pkey PRIMARY KEY (sp_id);


--
-- Name: hfj_spidx_quantity hfj_spidx_quantity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_quantity
    ADD CONSTRAINT hfj_spidx_quantity_pkey PRIMARY KEY (sp_id);


--
-- Name: hfj_spidx_string hfj_spidx_string_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_string
    ADD CONSTRAINT hfj_spidx_string_pkey PRIMARY KEY (sp_id);


--
-- Name: hfj_spidx_token hfj_spidx_token_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_token
    ADD CONSTRAINT hfj_spidx_token_pkey PRIMARY KEY (sp_id);


--
-- Name: hfj_spidx_uri hfj_spidx_uri_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_uri
    ADD CONSTRAINT hfj_spidx_uri_pkey PRIMARY KEY (sp_id);


--
-- Name: hfj_subscription_stats hfj_subscription_stats_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_subscription_stats
    ADD CONSTRAINT hfj_subscription_stats_pkey PRIMARY KEY (pid);


--
-- Name: hfj_tag_def hfj_tag_def_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_tag_def
    ADD CONSTRAINT hfj_tag_def_pkey PRIMARY KEY (tag_id);


--
-- Name: trm_concept idx_concept_cs_code; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept
    ADD CONSTRAINT idx_concept_cs_code UNIQUE (codesystem_pid, code);


--
-- Name: trm_concept_map idx_concept_map_url; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_map
    ADD CONSTRAINT idx_concept_map_url UNIQUE (url);


--
-- Name: trm_codesystem idx_cs_codesystem; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_codesystem
    ADD CONSTRAINT idx_cs_codesystem UNIQUE (code_system_uri);


--
-- Name: hfj_forced_id idx_forcedid_resid; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_forced_id
    ADD CONSTRAINT idx_forcedid_resid UNIQUE (resource_pid);


--
-- Name: hfj_forced_id idx_forcedid_type_resid; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_forced_id
    ADD CONSTRAINT idx_forcedid_type_resid UNIQUE (resource_type, resource_pid);


--
-- Name: hfj_idx_cmp_string_uniq idx_idxcmpstruniq_string; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_idx_cmp_string_uniq
    ADD CONSTRAINT idx_idxcmpstruniq_string UNIQUE (idx_string);


--
-- Name: hfj_history_tag idx_reshisttag_tagid; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_history_tag
    ADD CONSTRAINT idx_reshisttag_tagid UNIQUE (res_ver_pid, tag_id);


--
-- Name: hfj_res_param_present idx_resparmpresent_spid_resid; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_param_present
    ADD CONSTRAINT idx_resparmpresent_spid_resid UNIQUE (sp_id, res_id);


--
-- Name: hfj_res_tag idx_restag_tagid; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_tag
    ADD CONSTRAINT idx_restag_tagid UNIQUE (res_id, tag_id);


--
-- Name: hfj_res_ver idx_resver_id_ver; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_ver
    ADD CONSTRAINT idx_resver_id_ver UNIQUE (res_id, res_ver);


--
-- Name: hfj_search idx_search_uuid; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_search
    ADD CONSTRAINT idx_search_uuid UNIQUE (search_uuid);


--
-- Name: hfj_search_parm idx_searchparm_restype_spname; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_search_parm
    ADD CONSTRAINT idx_searchparm_restype_spname UNIQUE (res_type, param_name);


--
-- Name: hfj_search_result idx_searchres_order; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_search_result
    ADD CONSTRAINT idx_searchres_order UNIQUE (search_pid, search_order);


--
-- Name: hfj_subscription_stats idx_subsc_resid; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_subscription_stats
    ADD CONSTRAINT idx_subsc_resid UNIQUE (res_id);


--
-- Name: hfj_tag_def idx_tagdef_typesyscode; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_tag_def
    ADD CONSTRAINT idx_tagdef_typesyscode UNIQUE (tag_type, tag_system, tag_code);


--
-- Name: trm_codesystem trm_codesystem_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_codesystem
    ADD CONSTRAINT trm_codesystem_pkey PRIMARY KEY (pid);


--
-- Name: trm_codesystem_ver trm_codesystem_ver_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_codesystem_ver
    ADD CONSTRAINT trm_codesystem_ver_pkey PRIMARY KEY (pid);


--
-- Name: trm_concept_desig trm_concept_desig_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_desig
    ADD CONSTRAINT trm_concept_desig_pkey PRIMARY KEY (pid);


--
-- Name: trm_concept_map_group trm_concept_map_group_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_map_group
    ADD CONSTRAINT trm_concept_map_group_pkey PRIMARY KEY (pid);


--
-- Name: trm_concept_map_grp_element trm_concept_map_grp_element_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_map_grp_element
    ADD CONSTRAINT trm_concept_map_grp_element_pkey PRIMARY KEY (pid);


--
-- Name: trm_concept_map_grp_elm_tgt trm_concept_map_grp_elm_tgt_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_map_grp_elm_tgt
    ADD CONSTRAINT trm_concept_map_grp_elm_tgt_pkey PRIMARY KEY (pid);


--
-- Name: trm_concept_map trm_concept_map_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_map
    ADD CONSTRAINT trm_concept_map_pkey PRIMARY KEY (pid);


--
-- Name: trm_concept_pc_link trm_concept_pc_link_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_pc_link
    ADD CONSTRAINT trm_concept_pc_link_pkey PRIMARY KEY (pid);


--
-- Name: trm_concept trm_concept_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept
    ADD CONSTRAINT trm_concept_pkey PRIMARY KEY (pid);


--
-- Name: trm_concept_property trm_concept_property_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_property
    ADD CONSTRAINT trm_concept_property_pkey PRIMARY KEY (pid);


--
-- Name: idx_cncpt_map_grp_cd; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_cncpt_map_grp_cd ON trm_concept_map_grp_element USING btree (source_code);


--
-- Name: idx_cncpt_mp_grp_elm_tgt_cd; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_cncpt_mp_grp_elm_tgt_cd ON trm_concept_map_grp_elm_tgt USING btree (target_code);


--
-- Name: idx_concept_indexstatus; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_concept_indexstatus ON trm_concept USING btree (index_status);


--
-- Name: idx_forcedid_type_forcedid; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_forcedid_type_forcedid ON hfj_forced_id USING btree (resource_type, forced_id);


--
-- Name: idx_idxcmpstruniq_resource; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_idxcmpstruniq_resource ON hfj_idx_cmp_string_uniq USING btree (res_id);


--
-- Name: idx_indexstatus; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_indexstatus ON hfj_resource USING btree (sp_index_status);


--
-- Name: idx_res_date; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_res_date ON hfj_resource USING btree (res_updated);


--
-- Name: idx_res_lang; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_res_lang ON hfj_resource USING btree (res_type, res_language);


--
-- Name: idx_res_profile; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_res_profile ON hfj_resource USING btree (res_profile);


--
-- Name: idx_res_type; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_res_type ON hfj_resource USING btree (res_type);


--
-- Name: idx_resparmpresent_resid; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_resparmpresent_resid ON hfj_res_param_present USING btree (res_id);


--
-- Name: idx_resver_date; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_resver_date ON hfj_res_ver USING btree (res_updated);


--
-- Name: idx_resver_id_date; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_resver_id_date ON hfj_res_ver USING btree (res_id, res_updated);


--
-- Name: idx_resver_type_date; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_resver_type_date ON hfj_res_ver USING btree (res_type, res_updated);


--
-- Name: idx_rl_dest; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_rl_dest ON hfj_res_link USING btree (target_resource_id);


--
-- Name: idx_rl_src; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_rl_src ON hfj_res_link USING btree (src_resource_id);


--
-- Name: idx_rl_tpathres; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_rl_tpathres ON hfj_res_link USING btree (src_path, target_resource_id);


--
-- Name: idx_search_lastreturned; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_search_lastreturned ON hfj_search USING btree (search_last_returned);


--
-- Name: idx_search_restype_hashs; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_search_restype_hashs ON hfj_search USING btree (resource_type, search_query_string_hash, created);


--
-- Name: idx_sp_coords; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_coords ON hfj_spidx_coords USING btree (res_type, sp_name, sp_latitude, sp_longitude);


--
-- Name: idx_sp_coords_resid; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_coords_resid ON hfj_spidx_coords USING btree (res_id);


--
-- Name: idx_sp_coords_updated; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_coords_updated ON hfj_spidx_coords USING btree (sp_updated);


--
-- Name: idx_sp_date; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_date ON hfj_spidx_date USING btree (res_type, sp_name, sp_value_low, sp_value_high);


--
-- Name: idx_sp_date_resid; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_date_resid ON hfj_spidx_date USING btree (res_id);


--
-- Name: idx_sp_date_updated; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_date_updated ON hfj_spidx_date USING btree (sp_updated);


--
-- Name: idx_sp_number; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_number ON hfj_spidx_number USING btree (res_type, sp_name, sp_value);


--
-- Name: idx_sp_number_resid; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_number_resid ON hfj_spidx_number USING btree (res_id);


--
-- Name: idx_sp_number_updated; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_number_updated ON hfj_spidx_number USING btree (sp_updated);


--
-- Name: idx_sp_quantity; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_quantity ON hfj_spidx_quantity USING btree (res_type, sp_name, sp_system, sp_units, sp_value);


--
-- Name: idx_sp_quantity_resid; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_quantity_resid ON hfj_spidx_quantity USING btree (res_id);


--
-- Name: idx_sp_quantity_updated; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_quantity_updated ON hfj_spidx_quantity USING btree (sp_updated);


--
-- Name: idx_sp_string; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_string ON hfj_spidx_string USING btree (res_type, sp_name, sp_value_normalized);


--
-- Name: idx_sp_string_resid; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_string_resid ON hfj_spidx_string USING btree (res_id);


--
-- Name: idx_sp_string_updated; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_string_updated ON hfj_spidx_string USING btree (sp_updated);


--
-- Name: idx_sp_token; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_token ON hfj_spidx_token USING btree (res_type, sp_name, sp_system, sp_value);


--
-- Name: idx_sp_token_resid; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_token_resid ON hfj_spidx_token USING btree (res_id);


--
-- Name: idx_sp_token_unqual; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_token_unqual ON hfj_spidx_token USING btree (res_type, sp_name, sp_value);


--
-- Name: idx_sp_token_updated; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_token_updated ON hfj_spidx_token USING btree (sp_updated);


--
-- Name: idx_sp_uri; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_uri ON hfj_spidx_uri USING btree (res_type, sp_name, sp_uri);


--
-- Name: idx_sp_uri_coords; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_uri_coords ON hfj_spidx_uri USING btree (res_id);


--
-- Name: idx_sp_uri_restype_name; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_uri_restype_name ON hfj_spidx_uri USING btree (res_type, sp_name);


--
-- Name: idx_sp_uri_updated; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sp_uri_updated ON hfj_spidx_uri USING btree (sp_updated);


--
-- Name: hfj_spidx_date fk17s70oa59rm9n61k9thjqrsqm; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_date
    ADD CONSTRAINT fk17s70oa59rm9n61k9thjqrsqm FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_spidx_token fk7ulx3j1gg3v7maqrejgc7ybc4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_token
    ADD CONSTRAINT fk7ulx3j1gg3v7maqrejgc7ybc4 FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: trm_codesystem_ver fk_codesysver_cs_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_codesystem_ver
    ADD CONSTRAINT fk_codesysver_cs_id FOREIGN KEY (codesystem_pid) REFERENCES trm_codesystem(pid);


--
-- Name: trm_codesystem_ver fk_codesysver_res_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_codesystem_ver
    ADD CONSTRAINT fk_codesysver_res_id FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: trm_concept fk_concept_pid_cs_pid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept
    ADD CONSTRAINT fk_concept_pid_cs_pid FOREIGN KEY (codesystem_pid) REFERENCES trm_codesystem_ver(pid);


--
-- Name: trm_concept_desig fk_conceptdesig_concept; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_desig
    ADD CONSTRAINT fk_conceptdesig_concept FOREIGN KEY (concept_pid) REFERENCES trm_concept(pid);


--
-- Name: trm_concept_property fk_conceptprop_concept; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_property
    ADD CONSTRAINT fk_conceptprop_concept FOREIGN KEY (concept_pid) REFERENCES trm_concept(pid);


--
-- Name: hfj_forced_id fk_forcedid_resource; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_forced_id
    ADD CONSTRAINT fk_forcedid_resource FOREIGN KEY (resource_pid) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_history_tag fk_historytag_history; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_history_tag
    ADD CONSTRAINT fk_historytag_history FOREIGN KEY (res_ver_pid) REFERENCES hfj_res_ver(pid);


--
-- Name: hfj_idx_cmp_string_uniq fk_idxcmpstruniq_res_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_idx_cmp_string_uniq
    ADD CONSTRAINT fk_idxcmpstruniq_res_id FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_res_link fk_reslink_source; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_link
    ADD CONSTRAINT fk_reslink_source FOREIGN KEY (src_resource_id) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_res_link fk_reslink_target; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_link
    ADD CONSTRAINT fk_reslink_target FOREIGN KEY (target_resource_id) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_res_param_present fk_resparmpres_resid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_param_present
    ADD CONSTRAINT fk_resparmpres_resid FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_res_param_present fk_resparmpres_spid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_param_present
    ADD CONSTRAINT fk_resparmpres_spid FOREIGN KEY (sp_id) REFERENCES hfj_search_parm(pid);


--
-- Name: hfj_res_tag fk_restag_resource; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_tag
    ADD CONSTRAINT fk_restag_resource FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_search_include fk_searchinc_search; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_search_include
    ADD CONSTRAINT fk_searchinc_search FOREIGN KEY (search_pid) REFERENCES hfj_search(pid);


--
-- Name: hfj_search_result fk_searchres_res; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_search_result
    ADD CONSTRAINT fk_searchres_res FOREIGN KEY (resource_pid) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_search_result fk_searchres_search; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_search_result
    ADD CONSTRAINT fk_searchres_search FOREIGN KEY (search_pid) REFERENCES hfj_search(pid);


--
-- Name: hfj_spidx_string fk_spidxstr_resource; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_string
    ADD CONSTRAINT fk_spidxstr_resource FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_subscription_stats fk_subsc_resource_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_subscription_stats
    ADD CONSTRAINT fk_subsc_resource_id FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: trm_concept_map_grp_element fk_tcmgelement_group; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_map_grp_element
    ADD CONSTRAINT fk_tcmgelement_group FOREIGN KEY (concept_map_group_pid) REFERENCES trm_concept_map_group(pid);


--
-- Name: trm_concept_map_grp_elm_tgt fk_tcmgetarget_element; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_map_grp_elm_tgt
    ADD CONSTRAINT fk_tcmgetarget_element FOREIGN KEY (concept_map_grp_elm_pid) REFERENCES trm_concept_map_grp_element(pid);


--
-- Name: trm_concept_map_group fk_tcmgroup_conceptmap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_map_group
    ADD CONSTRAINT fk_tcmgroup_conceptmap FOREIGN KEY (concept_map_pid) REFERENCES trm_concept_map(pid);


--
-- Name: trm_concept_pc_link fk_term_conceptpc_child; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_pc_link
    ADD CONSTRAINT fk_term_conceptpc_child FOREIGN KEY (child_pid) REFERENCES trm_concept(pid);


--
-- Name: trm_concept_pc_link fk_term_conceptpc_cs; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_pc_link
    ADD CONSTRAINT fk_term_conceptpc_cs FOREIGN KEY (codesystem_pid) REFERENCES trm_codesystem_ver(pid);


--
-- Name: trm_concept_pc_link fk_term_conceptpc_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_pc_link
    ADD CONSTRAINT fk_term_conceptpc_parent FOREIGN KEY (parent_pid) REFERENCES trm_concept(pid);


--
-- Name: trm_codesystem fk_trmcodesystem_curver; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_codesystem
    ADD CONSTRAINT fk_trmcodesystem_curver FOREIGN KEY (current_version_pid) REFERENCES trm_codesystem_ver(pid);


--
-- Name: trm_codesystem fk_trmcodesystem_res; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_codesystem
    ADD CONSTRAINT fk_trmcodesystem_res FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: trm_concept_map fk_trmconceptmap_res; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trm_concept_map
    ADD CONSTRAINT fk_trmconceptmap_res FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_res_tag fkbfcjbaftmiwr3rxkwsy23vneo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_tag
    ADD CONSTRAINT fkbfcjbaftmiwr3rxkwsy23vneo FOREIGN KEY (tag_id) REFERENCES hfj_tag_def(tag_id);


--
-- Name: hfj_spidx_coords fkc97mpk37okwu8qvtceg2nh9vn; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_coords
    ADD CONSTRAINT fkc97mpk37okwu8qvtceg2nh9vn FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_spidx_number fkcltihnc5tgprj9bhpt7xi5otb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_number
    ADD CONSTRAINT fkcltihnc5tgprj9bhpt7xi5otb FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_spidx_uri fkgxsreutymmfjuwdswv3y887do; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_uri
    ADD CONSTRAINT fkgxsreutymmfjuwdswv3y887do FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_res_ver fkh20i7lcbchkaxekvwg9ix4hc5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_res_ver
    ADD CONSTRAINT fkh20i7lcbchkaxekvwg9ix4hc5 FOREIGN KEY (forced_id_pid) REFERENCES hfj_forced_id(pid);


--
-- Name: hfj_resource fkhjgj8cp879gfxko25cx5o692r; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_resource
    ADD CONSTRAINT fkhjgj8cp879gfxko25cx5o692r FOREIGN KEY (forced_id_pid) REFERENCES hfj_forced_id(pid);


--
-- Name: hfj_spidx_quantity fkn603wjjoi1a6asewxbbd78bi5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_spidx_quantity
    ADD CONSTRAINT fkn603wjjoi1a6asewxbbd78bi5 FOREIGN KEY (res_id) REFERENCES hfj_resource(res_id);


--
-- Name: hfj_history_tag fktderym7awj6q8iq5c51xv4ndw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY hfj_history_tag
    ADD CONSTRAINT fktderym7awj6q8iq5c51xv4ndw FOREIGN KEY (tag_id) REFERENCES hfj_tag_def(tag_id);


--
-- PostgreSQL database dump complete
--

