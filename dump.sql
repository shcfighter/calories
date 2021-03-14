--
-- PostgreSQL database dump
--

-- Dumped from database version 10.10
-- Dumped by pg_dump version 10.10

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: t_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.t_user (
    user_id bigint NOT NULL,
    user_name character varying(10),
    login_name character varying(50),
    mobile character varying(11),
    email character varying(50),
    open_id character varying(50),
    token character varying(64),
    password character varying(200),
    salt character varying(64),
    status smallint,
    is_deleted smallint,
    create_time timestamp(6) without time zone,
    update_time timestamp(6) without time zone,
    remarks character varying(255),
    versions bigint DEFAULT 0,
    session_key character varying(50)
);


ALTER TABLE public.t_user OWNER TO postgres;

--
-- Name: TABLE t_user; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.t_user IS '用户表';


--
-- Name: COLUMN t_user.user_name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.t_user.user_name IS '真实姓名';


--
-- Name: COLUMN t_user.login_name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.t_user.login_name IS '登录名';


--
-- Name: COLUMN t_user.mobile; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.t_user.mobile IS '手机号码';


--
-- Name: COLUMN t_user.email; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.t_user.email IS '邮箱';


--
-- Name: COLUMN t_user.open_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.t_user.open_id IS 'open_id';


--
-- Name: COLUMN t_user.token; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.t_user.token IS 'token';


--
-- Name: COLUMN t_user.salt; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.t_user.salt IS '加密盐';


--
-- Name: COLUMN t_user.status; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.t_user.status IS '状态 0=未激活；1=激活；2=禁用；-1=删除';


--
-- Data for Name: t_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.t_user (user_id, user_name, login_name, mobile, email, open_id, token, password, salt, status, is_deleted, create_time, update_time, remarks, versions, session_key) FROM stdin;
344279626660581376	\N	Zhie	\N	\N	oYDFW46tYoeMvtZJb1fN2h3_Q5Qc	9b6e7f4cdb4f3fe3ae8800054c43fe46	\N	\N	1	0	2019-07-04 22:05:25.072225	\N	\N	0	oOfjoIyuGZRhIfeQTqnN4w==
344708822666973184	\N	谢雅慧	\N	\N	oYDFW48gFn4YNWXv3MPIhCo_VA2E	0e7ee8c4567d53fbb807ceca358fd998	\N	\N	1	0	2019-07-06 02:30:53.338679	\N	\N	0	NBJfv4+uwKc51oxKsPL/xg==
354948217772838912	\N	周庭玮	\N	\N	oYDFW49FSerDuXhBNPXWc8EhKX1Q	a1299ccaba2bd6ebe6ebae52d2d28b0b	\N	\N	1	0	2019-08-03 08:38:35.372292	\N	\N	0	X7YTZEwWheS89M8fxIJJmg==
362510076302462976	\N	李佑郁	\N	\N	oYDFW44MTg2DrlY3Lt899rNhYYfc	21d88214b7e52a519deeaf97b8deff91	\N	\N	1	0	2019-08-24 05:26:42.966435	\N	\N	0	oI9itfnVfJxx2DLB5tsmvw==
365241890884423680	\N	王丽刚	\N	\N	oYDFW406uJp71yfEO_t7ES05eMIs	f28ca666c3c667d6047b21542f53f50e	\N	\N	1	0	2019-08-31 18:21:58.289229	\N	\N	2	Ph5wzcWAEJy8V0bwfp21bg==
357445999846887424	\N	王俞吟	\N	\N	oYDFW4_RK7TVW1TNFYpNIGb3edu0	5860f8b5a23a1dce0beb61f3f32de56e	\N	\N	1	0	2019-08-10 06:03:53.021451	\N	\N	0	Aa22m1P7GeAmwz07bdSRSg==
370179410600529920	\N	陈惠苹	\N	\N	oYDFW4zdvdliWvMv32Q93TiLDT-E	c27c4e1ebed92cc909bc322fd8c8f657	\N	\N	1	0	2019-09-14 09:21:54.742102	\N	\N	0	5M5YkRICx2Nm50BNRtrB+A==
346358577616785408	\N	洪子轩	\N	\N	oYDFW4_U9qGLR5Dx2Ovdx5WWGRVw	46563c40367a3c6e529168349c1c9352	\N	\N	1	0	2019-07-10 15:46:25.582323	\N	\N	0	ogEWMCImRWdaOdBBey5omQ==
346358732046864384	\N	张昆霖	\N	\N	oYDFW42IrVdlFsVam4ylNtMrpOP4	b62acf016821bfb3dd98d2fda0318a49	\N	\N	1	0	2019-07-10 15:47:02.387726	\N	\N	0	Rr6e/bMaW4AAhi4UrxiUkg==
346360764749189120	\N	林奕雯	\N	\N	oYDFW46sKcphoCeLPzeaqa5bkMuw	581f732a7bac2b795112d60029e8cd09	\N	\N	1	0	2019-07-10 15:55:07.021918	\N	\N	0	N+8U6QZoW1NSKMMgV72x7w==
346361566687531008	\N	陈良容	\N	\N	oYDFW45o6-ypytut0WGV3fDm1XW8	4077d9ce2f0b4068e84696ead28a1c6a	\N	\N	1	0	2019-07-10 15:58:18.219395	\N	\N	0	gYKy/o+X1aq1z1R3CWTS9g==
346361570839891968	\N	陈振侑	\N	\N	oYDFW4xLNkCtJAChwEOrEs_2aByc	c4fdb85b7b8c760648fd54ea1b75c894	\N	\N	1	0	2019-07-10 15:58:19.210381	\N	\N	0	8tAf9x8q32UqVFlw4gPlnw==
346361610543173632	\N	郭淑娟	\N	\N	oYDFW48Yf99TRd5mJ-wK3fQKlOMQ	c91f466c87c6a2cc8c397053cdb54450	\N	\N	1	0	2019-07-10 15:58:28.673619	\N	\N	0	r16x8RmeT9fR/yW2ih+PdQ==
346361864239845376	\N	林幸奇	\N	\N	oYDFW41wn4ziSmBYL07cGHO6dmJQ	9a6899ef01b4ce951e5a7b0577c84707	\N	\N	1	0	2019-07-10 15:59:29.161689	\N	\N	0	/laBbUh2p0/C638Wjk7qKg==
346361927821299712	\N	李佩冰	\N	\N	oYDFW4zRHKjRyjd74FSdvIezyN6U	448159f50f7fc2f9aef9308c06cbf01e	\N	\N	1	0	2019-07-10 15:59:44.321778	\N	\N	0	kHngs/O8FNJjvFaykHkcEQ==
346361942073544704	\N	陈宏达	\N	\N	oYDFW44e1W-repTJBIj5rw0jsBzc	74a2161ddaf69225f0a0e10f7a5e75ad	\N	\N	1	0	2019-07-10 15:59:47.717168	\N	\N	0	xO1Gb+2wUbewrsrAZlYH8A==
346362486175436800	\N	黄成依	\N	\N	oYDFW49AngdAcaPmaiyF1uZ7oWDg	7bf42553a6c3d32bfa83e884824832f6	\N	\N	1	0	2019-07-10 16:01:57.440268	\N	\N	0	5VUK4rHyWT2CS2ThcTYfFg==
346363081569472512	\N	乐志伟	\N	\N	oYDFW43_yiCzbqqaH77gI2ITfJfE	244d9e30662c98f8708948b7686083f1	\N	\N	1	0	2019-07-10 16:04:19.39404	\N	\N	0	RoW5pyCP3sPKjwVPCV8g/w==
346363299916550144	\N	施启岳	\N	\N	oYDFW470R3g13mc0AqxOVWTwILpA	128f02b5c96b55f9dfbcf8017c44c451	\N	\N	1	0	2019-07-10 16:05:11.451785	\N	\N	0	l4wE8tM19ghHf59PAAgpXg==
346363622299144192	\N	孙碧璇	\N	\N	oYDFW4y0DLyybVEgGg1p4wpZ9_jI	10fa3abd2b4868e524573c091dab1ebe	\N	\N	1	0	2019-07-10 16:06:28.313721	\N	\N	0	d7NesU88IZCAc6A9bnHD+g==
346363894907932672	\N	李宁宇	\N	\N	oYDFW4wXQdJGgG-0_kaL6w7MlbaU	22a83b30db1e40b206adc3eda7eb0e6a	\N	\N	1	0	2019-07-10 16:07:33.308057	\N	\N	0	pl5igRVFT8LjbhAaqRfkHQ==
346575916966744064	\N	林可怡	\N	\N	oYDFW4waXc7xW1Pe9L755LwnLy60	b5c831e1e3a381cafa1262098060774d	\N	\N	1	0	2019-07-11 06:10:03.30716	\N	\N	0	J+dMrr/t3V8RW1isAXNfNQ==
346618399268409344	\N	张志平	\N	\N	oYDFW47w-kKFfBwa9kLLCNdnljlE	7474cded279754c5a7d39035c23c6a6a	\N	\N	1	0	2019-07-11 08:58:51.879218	\N	\N	0	6OhV9r3a/F2x0BEJSzPAlg==
346938467198046208	\N	钱珊皓	\N	\N	oYDFW48Kpvu6D7roC7JK9YasWCik	c30c53c25c261e79278c945a6319f138	\N	\N	1	0	2019-07-12 06:10:42.018311	\N	\N	0	rkGLwBjEWnFeI3aihBQy+g==
346992423555174400	\N	李雅慧	\N	\N	oYDFW43of4FReaaKM2Ab95aqxMxw	27fb06ad77bee9f571197a3ff2bb9236	\N	\N	1	0	2019-07-12 09:45:06.21548	\N	\N	0	nk003e2C4na0BVO5hQ9jyg==
339673128252870656	\N	周怡梅	\N	\N	oYDFW4-t8Njrv0o9hFjVzKw1C2GA	884b17aba69308ebcd6268615ee9f4a6	\N	\N	1	0	2019-06-22 05:00:50.234598	\N	\N	0	ALcBBqHjtuvh3Lv6c0BkSw==
347104846794592256	\N	赵柏乐	\N	\N	oYDFW4245KK25jDmCaBBy14cZmCE	72bd5564987184dbbe50c2a0dfa6b8f0	\N	\N	1	0	2019-07-12 17:11:50.005198	\N	\N	0	zkxmgxyAylli/FFsuYDXyA==
347267212496736256	\N	张茹婷	\N	\N	oYDFW46HXlggD7IWGJVNcD88C1XM	ed8f49df3c02ba187ea0ce22860cc68c	\N	\N	1	0	2019-07-13 03:57:01.005107	\N	\N	0	J2SZtOML8wId516dmpMimw==
360048762992857088	\N	曹雅晴	\N	\N	oYDFW44u_AQz7oxuYpABbS7Ow9no	9d44f4fd2c7d27c587c00a00bfbd68e1	\N	\N	1	0	2019-08-17 10:26:20.213684	\N	\N	0	2kzjZtmeL3e1CgxnQMmCMA==
349790867009179648	\N	罗志杰	\N	\N	oYDFW48kxdr54ruZBmsLL3eyScbc	9e3e081ba651da5e3b1f6e549481ac40	\N	\N	1	0	2019-07-20 03:05:07.222584	\N	\N	0	wfZz8thwA337I6cXTSe1bw==
337315169535397888	\N	 		\N	oYDFW4-tenLzieOQmw2hNQ4b80w8	7ae55d2459c2a1ce68948ef2e6f54cfd	\N	\N	1	0	2019-06-15 16:51:09.211997	2019-06-15 17:13:40.401448	\N	35	BYw91SVstc8f7E8MiZ1e3g==
342194765577719808	\N	陈怡芳	\N	\N	oYDFW426DV_-pUSlC74eZg_CSdkQ	70d2368b6eb2205763ed7155e6146b9e	\N	\N	1	0	2019-06-29 04:00:55.421176	\N	\N	0	fUQ7pvip/jHwwOg1Ln2tyg==
365028415046291456	\N	吴雅惠	\N	\N	oYDFW4zrTVH-6ykzgj6caXvyO27A	ae76ebc7e37a13cdd79cf1a6a45bb7ed	\N	\N	1	0	2019-08-31 04:13:41.931372	\N	\N	0	yGk16VbGfT6OYI0HgouS2A==
350942651358515200	\N	北野	\N	\N	oYDFW4_O5NedNh15P_rGv_7DtB6c	5465ef0af71876fefa20e70a9eb8b9ef	\N	\N	1	0	2019-07-23 07:21:53.916821	\N	\N	0	IvuM/1E4gVXfbzwVVfHqAQ==
352352251861405696	\N	李佩财	\N	\N	oYDFW4--Hz_4v85fc6gOZXqF1IIA	67a109cee480efc44f5e7fa16248ea22	\N	\N	1	0	2019-07-27 04:43:08.864862	\N	\N	0	IMUhxCBzFVM2bJzx4X90ew==
352535144109838336	\N	吴嘉舜	\N	\N	oYDFW48HBVCzC2EbruwbBM5ZFjIM	fed1994ca7ffe1d43646a22e71770a0a	\N	\N	1	0	2019-07-27 16:49:53.791071	\N	\N	0	JWjySFtV0N1Bpc9splBa0g==
375163847037816832	\N	吴振豪	\N	\N	oYDFW4_RUIArd-JoURb8c64Da2vM	0bf81acc42ec4d9495806211f0ada11f	\N	\N	1	0	2019-09-28 03:28:17.070935	\N	\N	0	kZIju/71EumjYHcCeXGLWA==
353902253150703616	\N	龙政峰	\N	\N	oYDFW45podPY4eligI_HIdj67J70	e5b2e16c1ce9a8c16177f7fdf6ae726c	\N	\N	1	0	2019-07-31 11:22:17.970551	\N	\N	0	mBIIebldqMPVur4DciRnwA==
365265829174054912	\N	蒋世昌	\N	\N	oYDFW4zLjuv3EHM0MCbCjWp61B2U	eaa1d9304d8362aadd407f4453f815dd	\N	\N	1	0	2019-08-31 19:57:05.624386	\N	\N	0	I+lmpIidNyNT4UZ3sGs60w==
377910540816748544	\N	张慧敏	\N	\N	oYDFW4xrhjXY-RB5ZrJyrjnREuP0	200eae59499c45395682ff782c7d6d40	\N	\N	1	0	2019-10-05 17:22:39.801595	\N	\N	0	e7BSflI6h3ci7X//2ZpOjw==
377510666661138432	\N	李珮心	\N	\N	oYDFW4-fxfcDetiJP8w8rjNzPEVs	b5d13ca4850a17efed9145e57cc85b5d	\N	\N	1	0	2019-10-04 14:53:42.374761	\N	\N	1	FnD1NJ1zgL+kO7Y6R4s95Q==
365459153256321024	\N	单曲回放	10086	\N	oYDFW4wjA-8Osb73tc8kP7t1Dh28	97347106e3e0ad539dacd5516617604d	\N	\N	1	0	2019-09-01 08:45:17.675892	2019-10-08 19:34:11.353408	\N	7	1DdrruuiHpp30rrSZPe4Kg==
377519339789750272	\N	曹尚鸿	\N	\N	oYDFW49zEj3-zf61w49Fjx8omdzQ	8fdeaee8d507162fa184264f1f5471e8	\N	\N	1	0	2019-10-04 15:28:10.208781	\N	\N	0	Nt5lBukmFVfjWgY8CvsJDQ==
377752635522027520	\N	李宗霞	\N	\N	oYDFW48ovbctsP9dIknblhqlYUOU	b4ab5c2555eebf4ce84da1e0b200e2a7	\N	\N	1	0	2019-10-05 06:55:12.246863	\N	\N	0	nmcLw0ZixxCYsx9uLhtiwQ==
378247311165362176	\N	叶庭玮	\N	\N	oYDFW40m6UuRMSADtEMaHjnl2Pgs	27d2ec027d0d3ba1ffafe3ab09ad37d8	\N	\N	1	0	2019-10-06 15:40:52.109334	\N	\N	0	zL10vfwdIwsUZ+eBPqbmXA==
390929425748856832	\N	郭睿纬	\N	\N	oYDFW4zif6TmweBoP4iOWrgTSJPM	1a3c98bbab35653eea8d75ae407365bf	\N	\N	1	0	2019-11-10 15:35:03.861736	\N	\N	0	OuWCIu9n9zdWDdJa+ellfA==
402442300036354048	\N	李雅筑	\N	\N	oYDFW4zCHqhqUXOXLSXcpiUoWOUk	5bd103a225fbbc5c1bc2747d4c00400a	\N	\N	1	0	2019-12-12 10:03:06.987037	\N	\N	0	elmTLmA0R0QSuzFmvxfk7w==
407878531167686656	\N	许雅婷	\N	\N	oYDFW45NgA0dgcSrMTmNtCdMUqd8	e235b50481d22070f2c49cc5c1ae3da5	\N	\N	1	0	2019-12-27 10:04:45.483418	\N	\N	0	rr/OWnojxlKrHSSNHB6A2Q==
414414512016461824	\N	苏碧绮	\N	\N	oYDFW4_Zm1Wk0IdkDTX9RJsVRDOk	ca283437916cdafae137764576f38c5c	\N	\N	1	0	2020-01-14 10:56:24.750499	\N	\N	0	JvN76aBcMc9IDTXqc+MuLQ==
426730070539898880	\N	谢泰平	\N	\N	oYDFW45VlXe6bYemvfLMIxTE72rw	369e09b3a2e43c7b74fd3f1af22a24fa	\N	\N	1	0	2020-02-17 10:34:02.72955	\N	\N	0	p98nfnR5klW/gKonQ1KXog==
438251123938299904	\N	蔡怡君	\N	\N	oYDFW4-TUToAVU0t1wMlP_YxqJOE	5bcb4998288ff9325abb6dc8f9e8a1ae	\N	\N	1	0	2020-03-20 05:34:36.06352	\N	\N	0	ab7y5x2AfP0NaPdrMZrNuw==
439905119023271936	\N	A彭记辅材连锁～客服1	\N	\N	oYDFW446MBk5IGSlnOcwggQ0Ldvk	2aaeec3315338040cd45e6939bbe2fd5	\N	\N	1	0	2020-03-24 19:06:59.049286	\N	\N	1	XkwLjOo2+NYZIHUluw3x0A==
444027568715337728	\N	黄蓉芳	\N	\N	oYDFW4685gQHDOZEdpzx7haQxeMk	035b03bb122038716bfc8ff60ad9c4dd	\N	\N	1	0	2020-04-05 04:08:07.650217	\N	\N	0	1r0EUn6rVG8Xi3ZMe8o6pQ==
449962957804277760	\N	陈致希	\N	\N	oYDFW43IfVEb5hw6cYtNcd-_wPRc	420a5c999690710747e7db46bc6464f5	\N	\N	1	0	2020-04-21 13:13:14.69713	\N	\N	0	b3N5bq2XAGP5KFTW0ZJUHQ==
461465123945582592	\N	钱芝湖	\N	\N	oYDFW40nSyGnG5vnUXWzKvesDNxI	bb7050c7c8fc294fe8ad189e6780b679	\N	\N	1	0	2020-05-23 06:58:46.146707	\N	\N	0	XUedTFZxzUxaVpwamhu6kQ==
473494428317782016	\N	赖秉竹	\N	\N	oYDFW46DgcctrojHjQNRXQaRhT-0	11c559f720f8fd23d91baadb18870479	\N	\N	1	0	2020-06-25 11:38:54.532122	\N	\N	0	RvOBlQNz+GFp/KWQXfgJYA==
476658042490982400	\N	苏辛辉	\N	\N	oYDFW46f1Hace7IJzng9zsuJVi6Q	1a6423ac14afd4fc8a95397464e4edd1	\N	\N	1	0	2020-07-04 05:09:58.848977	\N	\N	0	+HHDRto0ZXTzAsPqX40s1A==
551324514114473984	\N	林芳江	\N	\N	oYDFW4-njEbUA56aa3ijcTPyUQbk	0d8e3759ad8d28c7a0e595cb4ef7d13f	\N	\N	1	0	2021-01-26 06:07:52.946403	\N	\N	0	2kikQUz54slqxiaDdCfsQA==
379234494818947072	\N	张珮瑜		\N	oYDFW46yfu4zYIuTFJWIb-NxnCvw	95c8f5dff6b537c7b5a558d9829a599d	\N	\N	1	0	2019-10-09 09:03:35.054503	2019-10-09 09:03:42.375634	\N	1	B+FpNNcnVi//HvYPu1CvaQ==
365288483968913408	\N	NullPointerException	10000	\N	oYDFW45ETe3r6k1eWREB3s5H9ozE	1eca3fb465410eea18dfe48cfdc7b61c	\N	\N	1	0	2019-08-31 21:27:06.94303	2019-10-18 15:05:59.388436	\N	166	B12biOV1G5UO58wuzlWIdw==
387231493044441088	\N	王佳慧	\N	\N	oYDFW47JR3vVndRa7B9WSvuxBiSQ	6c3e1051beedbc6060b9cb0bedc17592	\N	\N	1	0	2019-10-31 10:40:47.992078	\N	\N	0	LEMAvH9o2Ef4CjpBhB0z1w==
\.


--
-- Name: t_user `t_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.t_user
    ADD CONSTRAINT "`t_user_pkey" PRIMARY KEY (user_id);


--
-- PostgreSQL database dump complete
--

